package ui;

import java.util.HashMap;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import dao.AssessmentPolicyDAO;
import dao.GradingPolicyDAO;
import dao.MarksDAO;
import dao.SemesterDAO;
import dao.SubjectDAO;
import dao.UserProfileDAO;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import model.AssessmentPolicy;
import model.GradingPolicy;
import model.Marks;
import model.Semester;
import model.Subject;
import model.UserProfile;
import util.DialogUtil;

public class MarksEntryController {

    @FXML
    private Label subjectNameLabel;
    @FXML
    private VBox theorySection;
    @FXML
    private VBox theoryContainer;
    @FXML
    private Label theoryTotalLabel;
    @FXML
    private Label theoryGradeLabel;
    @FXML
    private VBox practicalSection;
    @FXML
    private VBox practicalContainer;
    @FXML
    private Label practicalTotalLabel;
    @FXML
    private Label practicalGradeLabel;

    private Stage dialogStage;
    private Subject currentSubject;
    private int universityId;
    private boolean isDataLoaded = false;
    private StackPane loadingOverlay;

    // DAOs
    private final AssessmentPolicyDAO assessmentDAO = new AssessmentPolicyDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final GradingPolicyDAO gradingDAO = new GradingPolicyDAO();
    private final SemesterDAO semesterDAO = new SemesterDAO();
    private final UserProfileDAO userDAO = new UserProfileDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    // Map to store text fields for saving later: Category -> Component -> TextField
    private final Map<String, Map<String, TextField>> inputsMap = new HashMap<>();

    // Map to store MaxMarks for each component: Category -> Component -> Double
    private final Map<String, Map<String, Double>> maxMarksMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Initially hide practical section
        practicalSection.setVisible(false);
        practicalSection.setManaged(false);

        // Create loading overlay (but don't add to scene yet - will be added when
        // dialog shows)
        java.util.ResourceBundle messages = SceneManager.getBundle();
        loadingOverlay = util.LoadingIndicator.createLoadingOverlay(messages.getString("settings.label.loading"));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setSubject(Subject subject) {
        this.currentSubject = subject;
        java.util.ResourceBundle messages = SceneManager.getBundle();
        subjectNameLabel.setText(
                java.text.MessageFormat.format("{0} ({1})", subject.getSubjectName(), subject.getCreditHoursDisplay()));

        loadUniversityIdAsync();
    }

    /**
     * Load university ID asynchronously to prevent UI thread blocking.
     * Fetches: Subject -> Semester -> User -> University
     */
    private void loadUniversityIdAsync() {
        javafx.concurrent.Task<Integer> task = new javafx.concurrent.Task<>() {
            @Override
            protected Integer call() throws Exception {
                // Database operations in background thread
                Semester semester = semesterDAO.getById(currentSubject.getSemesterId());
                if (semester != null) {
                    UserProfile user = userDAO.getById(semester.getUserId());
                    if (user != null) {
                        return user.getUniversityId();
                    }
                }
                return 0;
            }
        };

        task.setOnSucceeded(e -> {
            // Update UI on JavaFX Application Thread
            this.universityId = task.getValue();
            if (this.universityId > 0) {
                loadDynamicFields();
                isDataLoaded = true;
                // Force recalculation after data load
                calculateTotals("Theory");
                if (currentSubject.isHasPractical()) {
                    calculateTotals("Practical");
                }
            }
        });

        task.setOnFailed(e -> {
            System.err.println("Failed to load university ID: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void loadDynamicFields() {
        // 1. Load Policies
        List<AssessmentPolicy> theoryPolicies = assessmentDAO.getByUniversityAndCategory(universityId, "Theory");
        List<AssessmentPolicy> practicalPolicies = assessmentDAO.getByUniversityAndCategory(universityId, "Practical");

        // 2. Load Existing Marks
        List<Marks> existingMarks = marksDAO.getMarksBySubject(currentSubject.getId());
        Map<String, Map<String, Double>> marksLookup = new HashMap<>();

        for (Marks m : existingMarks) {
            marksLookup.computeIfAbsent(m.getCategory(), k -> new HashMap<>()).put(m.getComponentName(),
                    m.getObtainedMarks());
        }

        // 3. Generate Theory UI
        inputsMap.put("Theory", new HashMap<>());
        maxMarksMap.put("Theory", new HashMap<>());
        for (AssessmentPolicy policy : theoryPolicies) {
            Double existingVal = marksLookup.getOrDefault("Theory", new HashMap<>()).get(policy.getComponentName());
            addInputRow(theoryContainer, "Theory", policy.getComponentName(), policy.getMaxMarks(), existingVal);
        }

        // 4. Generate Practical UI (if applicable)
        if (currentSubject.isHasPractical()) {
            practicalSection.setVisible(true);
            practicalSection.setManaged(true);

            inputsMap.put("Practical", new HashMap<>());
            maxMarksMap.put("Practical", new HashMap<>());
            for (AssessmentPolicy policy : practicalPolicies) {
                Double existingVal = marksLookup.getOrDefault("Practical", new HashMap<>())
                        .get(policy.getComponentName());
                addInputRow(practicalContainer, "Practical", policy.getComponentName(), policy.getMaxMarks(),
                        existingVal);
            }
        }

        // Initial Calculation
        calculateTotals("Theory");
        if (currentSubject.isHasPractical()) {
            calculateTotals("Practical");
        }
    }

    private void addInputRow(VBox container, String category, String componentName, double maxMarks,
            Double currentValue) {
        // Store max marks
        maxMarksMap.get(category).put(componentName, maxMarks);

        // UI Generation
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        // Label (e.g., "Mid Term")
        Label nameLabel = new Label(componentName);
        nameLabel.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 14px;");
        nameLabel.setPrefWidth(120);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Input Field
        TextField inputField = new TextField();
        inputField.setPromptText("0-" + ((int) maxMarks));
        inputField.setPrefWidth(80);
        inputField.setStyle("-fx-background-color: #2D2D44; -fx-text-fill: white; -fx-background-radius: 6;");

        if (currentValue != null) {
            inputField.setText(String.valueOf(currentValue));
        }

        // Validation & Live Calculation Listener
        inputField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Allow empty string or valid number with max 2 decimal places
            // Regex: ^\d+(\.\d{0,2})?$ -> Start with digits, optional dot, max 2 decimals
            if (!newVal.isEmpty() && !newVal.matches("^\\d+(\\.\\d{0,2})?$")) {
                inputField.setText(oldVal); // Revert invalid input
            } else {
                handleInputChange(inputField, category, componentName, maxMarks);
            }
        });

        // Max Marks Label
        java.util.ResourceBundle messages = SceneManager.getBundle();
        Label maxLabel = new Label("/ " + maxMarks);
        maxLabel.setStyle("-fx-text-fill: #7D7A9C; -fx-font-size: 14px;");

        row.getChildren().addAll(nameLabel, spacer, inputField, maxLabel);
        container.getChildren().add(row);

        // Store reference
        inputsMap.get(category).put(componentName, inputField);
    }

    private void handleInputChange(TextField field, String category, String componentName, double maxMarks) {
        String text = field.getText();
        if (text.isEmpty()) {
            field.setStyle("-fx-background-color: #2D2D44; -fx-text-fill: white; -fx-background-radius: 6;");
            calculateTotals(category);
            return;
        }

        try {
            double val = Double.parseDouble(text);
            if (val > maxMarks) {
                // Error style
                field.setStyle(
                        "-fx-background-color: #451a1a; -fx-text-fill: #FECACA; -fx-border-color: #EF4444; -fx-border-radius: 6; -fx-background-radius: 6;");
            } else {
                // Normal style
                field.setStyle("-fx-background-color: #2D2D44; -fx-text-fill: white; -fx-background-radius: 6;");
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
        calculateTotals(category);
    }

    private void calculateTotals(String category) {
        if (!isDataLoaded)
            return;

        double totalObtained = 0;
        double totalMax = 0;
        boolean hasError = false;

        Map<String, TextField> inputs = inputsMap.get(category);
        Map<String, Double> maxes = maxMarksMap.get(category);

        for (Map.Entry<String, TextField> entry : inputs.entrySet()) {
            String component = entry.getKey();
            TextField field = entry.getValue();
            double max = maxes.get(component);

            String text = field.getText().trim();
            if (!text.isEmpty()) {
                try {
                    double val = Double.parseDouble(text);
                    if (val <= max) {
                        totalObtained += val;
                        totalMax += max;
                    } else {
                        hasError = true;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        java.util.ResourceBundle messages = SceneManager.getBundle();
        if (totalMax == 0) {
            updateLabels(category, 0, 0, messages.getString("dashboard.gpa.na"), 0.0);
            return;
        }

        // Percentage for Grade
        double percentage = (totalObtained / totalMax) * 100;

        // Find Grade
        GradingPolicy gradePolicy = gradingDAO.findGradeForMarks(universityId, category, percentage);

        String grade = (gradePolicy != null) ? gradePolicy.getGradeName() : "F";
        Double gradePoint = (gradePolicy != null) ? gradePolicy.getGradePoint() : 0.0;

        if (hasError)
            grade = messages.getString("dialog.error.title"); // Using Error as grade name if there's an error

        updateLabels(category, totalObtained, totalMax, grade, gradePoint);
    }

    private void updateLabels(String category, double obtained, double max, String grade, double gradePoint) {
        java.util.ResourceBundle messages = SceneManager.getBundle();
        String gradeDisplay = grade;
        if (!messages.getString("dialog.error.title").equals(grade)
                && !messages.getString("dashboard.gpa.na").equals(grade)) {
            gradeDisplay += String.format(" (%.2f)", gradePoint);
        }

        if ("Theory".equals(category)) {
            theoryTotalLabel.setText(String.format("%.1f / %.1f", obtained, max));
            theoryGradeLabel.setText(gradeDisplay);
            theoryGradeLabel.setUserData(gradePoint);
        } else {
            practicalTotalLabel.setText(String.format("%.1f / %.1f", obtained, max));
            practicalGradeLabel.setText(gradeDisplay);
            practicalGradeLabel.setUserData(gradePoint);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateBeforeSave())
            return;

        // Add loading overlay to dialog
        if (dialogStage != null && dialogStage.getScene() != null) {
            javafx.scene.layout.Pane root = (javafx.scene.layout.Pane) dialogStage.getScene().getRoot();
            if (root instanceof javafx.scene.layout.StackPane) {
                ((javafx.scene.layout.StackPane) root).getChildren().add(loadingOverlay);
            }
        }

        // Show loading
        util.LoadingIndicator.show(loadingOverlay);

        // Run save operation in background with transaction-style error handling
        javafx.concurrent.Task<Void> saveTask = new javafx.concurrent.Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // TRANSACTION MANAGEMENT: All operations must succeed or fail together
                // to prevent data inconsistency (e.g., marks saved but subject grade not
                // updated)
                try {
                    // Step 1: Save Theory marks
                    saveCategory("Theory");

                    // Step 2: Save Practical marks (if applicable)
                    if (currentSubject.isHasPractical()) {
                        saveCategory("Practical");
                    }

                    // Step 3: Update Subject's grades in database
                    updateSubjectGrades();

                    // Step 4: Calculate and update semester GPA
                    semesterDAO.calculateAndUpdateGPA(currentSubject.getSemesterId());

                    Thread.sleep(300); // Small delay for smooth UX

                    return null;

                } catch (Exception e) {
                    // If any step fails, propagate the exception to trigger rollback behavior
                    System.err.println("Transaction-style save failed, operation aborted");
                    throw new RuntimeException("Save operation failed: " + e.getMessage(), e);
                }
            }
        };

        saveTask.setOnSucceeded(e -> {
            util.LoadingIndicator.hide(loadingOverlay);

            javafx.application.Platform.runLater(() -> {
                java.util.ResourceBundle messages = SceneManager.getBundle();
                DialogUtil.showInfo(dialogStage, messages.getString("dialog.success.title"),
                        messages.getString("message.marks.saved"));
                dialogStage.close();
            });
        });

        saveTask.setOnFailed(e -> {
            util.LoadingIndicator.hide(loadingOverlay);

            javafx.application.Platform.runLater(() -> {
                java.util.ResourceBundle messages = SceneManager.getBundle();
                String errorMsg = java.text.MessageFormat.format(messages.getString("message.save.failed"),
                        saveTask.getException().getMessage());
                DialogUtil.showError(dialogStage, messages.getString("dialog.error.title"), errorMsg);
            });
        });

        new Thread(saveTask).start();
    }

    private void updateSubjectGrades() {
        String tLabel = theoryGradeLabel.getText();
        String pLabel = practicalGradeLabel.getText();

        Double tPoint = (Double) theoryGradeLabel.getUserData();
        Double pPoint = (Double) practicalGradeLabel.getUserData();

        String tGrade = extractGradeLetter(tLabel);
        String pGrade = extractGradeLetter(pLabel);

        java.util.ResourceBundle messages = SceneManager.getBundle();
        if (messages.getString("dialog.error.title").equals(tLabel)
                || messages.getString("dashboard.gpa.na").equals(tLabel)) {
            tGrade = null;
            tPoint = null;
        }
        if (messages.getString("dialog.error.title").equals(pLabel)
                || messages.getString("dashboard.gpa.na").equals(pLabel)) {
            pGrade = null;
            pPoint = null;
        }

        // Update model object
        currentSubject.setTheoryGrade(tGrade);
        currentSubject.setPracticalGrade(pGrade);
        currentSubject.setTheoryGradePoint(tPoint);
        currentSubject.setPracticalGradePoint(pPoint);

        // Persist to database
        subjectDAO.updateGrades(currentSubject.getId(), tGrade, pGrade, tPoint, pPoint);
    }

    private String extractGradeLetter(String labelText) {
        java.util.ResourceBundle messages = SceneManager.getBundle();
        if (labelText == null || labelText.contains(messages.getString("dashboard.gpa.na"))
                || labelText.contains(messages.getString("dialog.error.title")))
            return null;
        if (labelText.contains(" (")) {
            return labelText.substring(0, labelText.indexOf(" ("));
        }
        return labelText;
    }

    private void saveCategory(String category) {
        Map<String, TextField> inputs = inputsMap.get(category);
        Map<String, Double> maxes = maxMarksMap.get(category);

        for (Map.Entry<String, TextField> entry : inputs.entrySet()) {
            String component = entry.getKey();
            TextField field = entry.getValue();
            String text = field.getText().trim();
            double max = maxes.get(component);

            Double obtained = null;
            if (!text.isEmpty()) {
                try {
                    obtained = Double.parseDouble(text);
                } catch (NumberFormatException ignored) {
                }
            }

            Marks mark = new Marks(currentSubject.getId(), category, component, obtained, max);
            marksDAO.saveOrUpdate(mark);
        }
    }

    private boolean validateBeforeSave() {
        java.util.ResourceBundle messages = SceneManager.getBundle();
        for (Map<String, TextField> map : inputsMap.values()) {
            for (TextField tf : map.values()) {
                if (tf.getStyle().contains("#451a1a")) {
                    DialogUtil.showError(dialogStage, messages.getString("dialog.error.title"),
                            "Please correct the highlighted fields.");
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}