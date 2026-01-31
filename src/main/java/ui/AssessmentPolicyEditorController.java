package ui;

import dao.AssessmentPolicyDAO;
import dao.UniversityDAO;
import dao.UserProfileDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.AssessmentPolicy;
import model.University;
import model.UserProfile;

import java.util.List;

public class AssessmentPolicyEditorController {

    @FXML
    private Label universityNameLabel;

    @FXML
    private ToggleButton theoryButton;

    @FXML
    private ToggleButton labButton;

    @FXML
    private TableView<AssessmentPolicyRow> assessmentTable;

    @FXML
    private TableColumn<AssessmentPolicyRow, String> componentNameCol;

    @FXML
    private TableColumn<AssessmentPolicyRow, Double> maxMarksCol;

    @FXML
    private TableColumn<AssessmentPolicyRow, Void> actionsCol;

    @FXML
    private Label totalLabel;

    @FXML
    private VBox validationBox;

    @FXML
    private Label validationLabel;

    @FXML
    private VBox warningBox;

    @FXML
    private Label warningLabel;

    private Stage dialogStage;
    private UserProfile currentUser;
    private UserProfileDAO userDAO;
    private UniversityDAO universityDAO;
    private AssessmentPolicyDAO assessmentDAO;
    private boolean saveClicked = false;
    private String currentCategory = "Theory";

    private final ObservableList<AssessmentPolicyRow> assessmentData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        userDAO = new UserProfileDAO();
        universityDAO = new UniversityDAO();
        assessmentDAO = new AssessmentPolicyDAO();
        currentUser = userDAO.getUser();

        if (currentUser != null) {
            University uni = universityDAO.getById(currentUser.getUniversityId());
            if (uni != null) {
                universityNameLabel.setText(uni.getName());
            }
        }

        // Setup toggle buttons
        ToggleGroup categoryGroup = new ToggleGroup();
        theoryButton.setToggleGroup(categoryGroup);
        labButton.setToggleGroup(categoryGroup);

        theoryButton.setSelected(true);
        currentCategory = "Theory";
        updateButtonStyles();

        categoryGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                currentCategory = newToggle == theoryButton ? "Theory" : "Practical";
                updateButtonStyles();
                loadAssessmentData(currentCategory);
            }
        });

        setupTable();
        loadAssessmentData(currentCategory);
    }

    private void updateButtonStyles() {
        String selectedStyle = "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 24; -fx-cursor: hand;";
        String unselectedStyle = "-fx-background-color: #2D2D44; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 24; -fx-cursor: hand;";

        theoryButton.setStyle(theoryButton.isSelected() ? selectedStyle : unselectedStyle);
        labButton.setStyle(labButton.isSelected() ? selectedStyle : unselectedStyle);
    }

    private void setupTable() {
        // Component Name Column (Editable)
        componentNameCol.setCellValueFactory(cellData -> cellData.getValue().componentNameProperty());
        componentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        componentNameCol.setOnEditCommit(event -> {
            event.getRowValue().setComponentName(event.getNewValue());
            validateAssessment();
        });

        // Max Marks Column (Editable)
        maxMarksCol.setCellValueFactory(cellData -> cellData.getValue().maxMarksProperty().asObject());
        maxMarksCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        maxMarksCol.setOnEditCommit(event -> {
            event.getRowValue().setMaxMarks(event.getNewValue());
            updateTotal();
            validateAssessment();
        });

        // Actions Column (Delete Button)
        actionsCol.setCellFactory(param -> new TableCell<AssessmentPolicyRow, Void>() {
            private final Button deleteBtn = new Button("🗑️");

            {
                deleteBtn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-size: 16px; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    AssessmentPolicyRow row = getTableRow().getItem();
                    if (row != null) {
                        assessmentData.remove(row);
                        updateTotal();
                        validateAssessment();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        assessmentTable.setItems(assessmentData);
    }

    private void loadAssessmentData(String category) {
        assessmentData.clear();

        if (currentUser != null) {
            List<AssessmentPolicy> policies = assessmentDAO.getByUniversityAndCategory(
                    currentUser.getUniversityId(), category);
            for (AssessmentPolicy policy : policies) {
                assessmentData.add(new AssessmentPolicyRow(
                        policy.getId(),
                        policy.getComponentName(),
                        policy.getMaxMarks()));
            }
        }

        updateTotal();
        validateAssessment();
    }

    @FXML
    private void handleAddComponent() {
        assessmentData.add(new AssessmentPolicyRow(-1, "New Component", 0.0));
        updateTotal();
        validateAssessment();
    }

    private void updateTotal() {
        double total = assessmentData.stream()
                .mapToDouble(AssessmentPolicyRow::getMaxMarks)
                .sum();

        java.util.ResourceBundle messages = SceneManager.getBundle();
        totalLabel.setText(
                java.text.MessageFormat.format("{0} {1} / 100", messages.getString("report.label.total"), total));

        // Update total label color
        if (total == 100) {
            totalLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 13px; -fx-font-weight: bold;");
        } else {
            totalLabel.setStyle("-fx-text-fill: #FFA366; -fx-font-size: 13px; -fx-font-weight: bold;");
        }
    }

    private void validateAssessment() {
        StringBuilder errors = new StringBuilder();

        if (assessmentData.isEmpty()) {
            errors.append("• At least one component must be defined\n");
        }

        // Check for duplicate component names
        for (int i = 0; i < assessmentData.size(); i++) {
            AssessmentPolicyRow row = assessmentData.get(i);

            java.util.ResourceBundle messages = SceneManager.getBundle();
            if (row.getComponentName().trim().isEmpty()) {
                errors.append("• ").append(messages.getString("report.table.subject")).append(" ").append(i + 1)
                        .append(": ").append(messages.getString("subject.label.name.empty")).append("\n");
            }

            if (row.getMaxMarks() <= 0) {
                errors.append("• ").append(row.getComponentName()).append(": Max marks must be greater than 0\n");
            }

            // Check for duplicates
            for (int j = i + 1; j < assessmentData.size(); j++) {
                AssessmentPolicyRow other = assessmentData.get(j);
                if (row.getComponentName().trim().equalsIgnoreCase(other.getComponentName().trim())) {
                    errors.append("• Duplicate component name: ").append(row.getComponentName()).append("\n");
                }
            }
        }

        // Show/hide validation box
        if (errors.length() > 0) {
            validationLabel.setText(errors.toString());
            validationBox.setVisible(true);
            validationBox.setManaged(true);
        } else {
            validationBox.setVisible(false);
            validationBox.setManaged(false);
        }

        // Check total and show warning
        double total = assessmentData.stream()
                .mapToDouble(AssessmentPolicyRow::getMaxMarks)
                .sum();

        java.util.ResourceBundle messages = SceneManager.getBundle();
        if (total != 100 && errors.length() == 0) {
            warningLabel.setText(
                    java.text.MessageFormat.format(messages.getString("settings.assessment.warning.total"), total));
            warningBox.setVisible(true);
            warningBox.setManaged(true);
        } else {
            warningBox.setVisible(false);
            warningBox.setManaged(false);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        validateAssessment();

        if (validationBox.isVisible()) {
            util.DialogUtil.showError(dialogStage, "Validation Error",
                    "Please fix the validation errors before saving.");
            return;
        }

        java.util.ResourceBundle messages = SceneManager.getBundle();
        // Confirm with user
        boolean confirmed = util.DialogUtil.showConfirmation(dialogStage,
                messages.getString("settings.assessment.confirm.title"),
                messages.getString("settings.assessment.confirm.message"));

        if (!confirmed) {
            return;
        }

        // Save changes
        assessmentDAO.deleteByUniversityAndCategory(currentUser.getUniversityId(), currentCategory);

        for (AssessmentPolicyRow row : assessmentData) {
            assessmentDAO.save(new AssessmentPolicy(
                    currentUser.getUniversityId(),
                    currentCategory,
                    row.getComponentName(),
                    row.getMaxMarks()));
        }

        System.out.println("Assessment policy saved for " + currentCategory);

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    // Inner class for table rows
    public static class AssessmentPolicyRow {
        private final SimpleStringProperty componentName;
        private final SimpleDoubleProperty maxMarks;
        private int id;

        public AssessmentPolicyRow(int id, String componentName, double maxMarks) {
            this.id = id;
            this.componentName = new SimpleStringProperty(componentName);
            this.maxMarks = new SimpleDoubleProperty(maxMarks);
        }

        public int getId() {
            return id;
        }

        public String getComponentName() {
            return componentName.get();
        }

        public void setComponentName(String value) {
            componentName.set(value);
        }

        public SimpleStringProperty componentNameProperty() {
            return componentName;
        }

        public double getMaxMarks() {
            return maxMarks.get();
        }

        public void setMaxMarks(double value) {
            maxMarks.set(value);
        }

        public SimpleDoubleProperty maxMarksProperty() {
            return maxMarks;
        }
    }
}