package ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AssessmentPolicyDAO;
import dao.GradingPolicyDAO;
import dao.MarksDAO;
import dao.SemesterDAO;
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

    // DAOs
    private final AssessmentPolicyDAO assessmentDAO = new AssessmentPolicyDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final GradingPolicyDAO gradingDAO = new GradingPolicyDAO();
    private final SemesterDAO semesterDAO = new SemesterDAO();
    private final UserProfileDAO userDAO = new UserProfileDAO();

    // Map to store text fields for saving later: Category -> Component -> TextField
    private final Map<String, Map<String, TextField>> inputsMap = new HashMap<>();

    // Map to store MaxMarks for each component: Category -> Component -> Double
    private final Map<String, Map<String, Double>> maxMarksMap = new HashMap<>();

    @FXML
    public void initialize() {
        // Initially hide practical section
        practicalSection.setVisible(false);
        practicalSection.setManaged(false);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setSubject(Subject subject) {
        this.currentSubject = subject;
        subjectNameLabel.setText(subject.getSubjectName() + " (" + subject.getCreditHoursDisplay() + ")");

        ltc1qs49erv7pzeczp5qlnxd46aufzapsmzpa7y73ct();
    }

    private void ltc1qs49erv7pzeczp5qlnxd46aufzapsmzpa7y73ct() {
        // Fetch University ID chain: Subject -> Semester -> User -> University
        Semester semester = semesterDAO.getById(currentSubject.getSemesterId());
        if (semester != null) {
            UserProfile user = userDAO.getById(semester.getUserId());
            if (user != null) {
                this.universityId = user.getUniversityId();
                loadDynamicFields();
                isDataLoaded = true;
                // Force recalculation after data load
                calculateTotals("Theory");
                if (currentSubject.isHasPractical()) {
                    calculateTotals("Practical");
                }
            }
        }
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
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                inputField.setText(oldVal); // Enforce numeric
            } else {
                handleInputChange(inputField, category, componentName, maxMarks);
            }
        });

        // Max Marks Label
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

        if (totalMax == 0) {
            updateLabels(category, 0, 0, "N/A", 0.0);
            return;
        }

        // Percentage for Grade
        double percentage = (totalObtained / totalMax) * 100;

        // Find Grade
        GradingPolicy gradePolicy = gradingDAO.findGradeForMarks(universityId, category, percentage);

        String grade = (gradePolicy != null) ? gradePolicy.getGradeName() : "F";
        Double gradePoint = (gradePolicy != null) ? gradePolicy.getGradePoint() : 0.0;

        if (hasError)
            grade = "Error";

        updateLabels(category, totalObtained, totalMax, grade, gradePoint);
    }

    private void updateLabels(String category, double obtained, double max, String grade, double gradePoint) {
        String gradeDisplay = grade;
        if (!"Error".equals(grade) && !"N/A".equals(grade)) {
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

        // Save Theory
        saveCategory("Theory");

        // Save Practical
        if (currentSubject.isHasPractical()) {
            saveCategory("Practical");
        }

        // Update Subject's grades in database
        updateSubjectGrades();

        // ===== PHASE 6: AUTO-CALCULATE SEMESTER GPA =====
        semesterDAO.calculateAndUpdateGPA(currentSubject.getSemesterId());
        System.out.println("Phase 6: GPA auto-calculated for semester ID: " + currentSubject.getSemesterId());

        DialogUtil.showInfo(dialogStage, "Success", "Marks saved and GPA calculated successfully!");
        dialogStage.close();
    }

    private void updateSubjectGrades() {
        String tLabel = theoryGradeLabel.getText();
        String pLabel = practicalGradeLabel.getText();

        Double tPoint = (Double) theoryGradeLabel.getUserData();
        Double pPoint = (Double) practicalGradeLabel.getUserData();

        String tGrade = extractGradeLetter(tLabel);
        String pGrade = extractGradeLetter(pLabel);

        if ("Error".equals(tLabel) || "N/A".equals(tLabel)) {
            tGrade = null;
            tPoint = null;
        }
        if ("Error".equals(pLabel) || "N/A".equals(pLabel)) {
            pGrade = null;
            pPoint = null;
        }

        // Update model object
        currentSubject.setTheoryGrade(tGrade);
        currentSubject.setPracticalGrade(pGrade);
        currentSubject.setTheoryGradePoint(tPoint);
        currentSubject.setPracticalGradePoint(pPoint);

        // Persist to database
        new dao.SubjectDAO().updateGrades(currentSubject.getId(), tGrade, pGrade, tPoint, pPoint);
    }

    private String extractGradeLetter(String labelText) {
        if (labelText == null || labelText.contains("N/A") || labelText.contains("Error"))
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
        for (Map<String, TextField> map : inputsMap.values()) {
            for (TextField tf : map.values()) {
                if (tf.getStyle().contains("#451a1a")) {
                    DialogUtil.showError(dialogStage, "Invalid Input", "Please correct the highlighted fields.");
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