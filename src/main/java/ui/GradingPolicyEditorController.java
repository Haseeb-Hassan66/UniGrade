package ui;

import dao.GradingPolicyDAO;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.GradingPolicy;
import model.University;
import model.UserProfile;

import java.util.List;

public class GradingPolicyEditorController {

    @FXML
    private Label universityNameLabel;

    @FXML
    private ToggleButton theoryButton;

    @FXML
    private ToggleButton labButton;

    @FXML
    private TableView<GradingPolicyRow> gradingTable;

    @FXML
    private TableColumn<GradingPolicyRow, String> gradeNameCol;

    @FXML
    private TableColumn<GradingPolicyRow, Double> minMarksCol;

    @FXML
    private TableColumn<GradingPolicyRow, Double> maxMarksCol;

    @FXML
    private TableColumn<GradingPolicyRow, Double> gradePointCol;

    @FXML
    private TableColumn<GradingPolicyRow, Void> actionsCol;

    @FXML
    private VBox validationBox;

    @FXML
    private Label validationLabel;

    private Stage dialogStage;
    private UserProfile currentUser;
    private UserProfileDAO userDAO;
    private UniversityDAO universityDAO;
    private GradingPolicyDAO gradingDAO;
    private boolean saveClicked = false;
    private String currentCategory = "Theory";

    private final ObservableList<GradingPolicyRow> gradingData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        userDAO = new UserProfileDAO();
        universityDAO = new UniversityDAO();
        gradingDAO = new GradingPolicyDAO();
        currentUser = userDAO.getUser();

        if (currentUser != null) {
            University uni = universityDAO.getById(currentUser.getUniversityId());
            if (uni != null) {
                universityNameLabel.setText(uni.getName());
            }
        }

        // --- ToggleGroup logic ---
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
                loadGradingData(currentCategory);
            }
        });

        setupTable();
        loadGradingData(currentCategory);
    }

    private void updateButtonStyles() {
        String selectedStyle = "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 24; -fx-cursor: hand;";
        String unselectedStyle = "-fx-background-color: #2D2D44; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 24; -fx-cursor: hand;";

        theoryButton.setStyle(theoryButton.isSelected() ? selectedStyle : unselectedStyle);
        labButton.setStyle(labButton.isSelected() ? selectedStyle : unselectedStyle);
    }

    private void setupTable() {
        gradeNameCol.setCellValueFactory(cellData -> cellData.getValue().gradeNameProperty());
        gradeNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        gradeNameCol.setOnEditCommit(event -> {
            event.getRowValue().setGradeName(event.getNewValue());
            validateGrading();
        });

        minMarksCol.setCellValueFactory(cellData -> cellData.getValue().minMarksProperty().asObject());
        minMarksCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        minMarksCol.setOnEditCommit(event -> {
            event.getRowValue().setMinMarks(event.getNewValue());
            validateGrading();
        });

        maxMarksCol.setCellValueFactory(cellData -> cellData.getValue().maxMarksProperty().asObject());
        maxMarksCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        maxMarksCol.setOnEditCommit(event -> {
            event.getRowValue().setMaxMarks(event.getNewValue());
            validateGrading();
        });

        gradePointCol.setCellValueFactory(cellData -> cellData.getValue().gradePointProperty().asObject());
        gradePointCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        gradePointCol.setOnEditCommit(event -> {
            event.getRowValue().setGradePoint(event.getNewValue());
            validateGrading();
        });

        actionsCol.setCellFactory(param -> new TableCell<GradingPolicyRow, Void>() {
            private final Button deleteBtn = new Button("🗑️");

            {
                deleteBtn.setStyle(
                        "-fx-background-color: transparent; -fx-text-fill: #EF4444; -fx-font-size: 16px; -fx-cursor: hand;");
                deleteBtn.setOnAction(event -> {
                    GradingPolicyRow row = getTableRow().getItem();
                    if (row != null) {
                        gradingData.remove(row);
                        validateGrading();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });

        gradingTable.setItems(gradingData);
    }

    private void loadGradingData(String category) {
        gradingData.clear();

        if (currentUser != null) {
            List<GradingPolicy> policies = gradingDAO.getByUniversityAndCategory(currentUser.getUniversityId(),
                    category);
            for (GradingPolicy policy : policies) {
                gradingData.add(new GradingPolicyRow(
                        policy.getId(),
                        policy.getGradeName(),
                        policy.getMinMarks(),
                        policy.getMaxMarks(),
                        policy.getGradePoint()));
            }
        }

        validateGrading();
    }

    @FXML
    private void handleAddGrade() {
        gradingData.add(new GradingPolicyRow(-1, "NEW", 0.0, 0.0, 0.0));
        validateGrading();
    }

    private void validateGrading() {
        StringBuilder errors = new StringBuilder();

        if (gradingData.isEmpty())
            errors.append("• At least one grade must be defined\n");

        for (int i = 0; i < gradingData.size(); i++) {
            GradingPolicyRow row = gradingData.get(i);

            if (row.getGradeName().trim().isEmpty())
                errors.append("• Row ").append(i + 1).append(": Grade name cannot be empty\n");
            if (row.getMinMarks() >= row.getMaxMarks())
                errors.append("• ").append(row.getGradeName()).append(": Min marks must be less than max marks\n");
            if (row.getMinMarks() < 0 || row.getMaxMarks() < 0)
                errors.append("• ").append(row.getGradeName()).append(": Marks cannot be negative\n");

            for (int j = i + 1; j < gradingData.size(); j++) {
                GradingPolicyRow other = gradingData.get(j);
                if (rangesOverlap(row.getMinMarks(), row.getMaxMarks(), other.getMinMarks(), other.getMaxMarks())) {
                    errors.append("• ").append(row.getGradeName()).append(" and ").append(other.getGradeName())
                            .append(" have overlapping ranges\n");
                }
            }
        }

        boolean covers0 = gradingData.stream().anyMatch(r -> r.getMinMarks() == 0);
        boolean covers100 = gradingData.stream().anyMatch(r -> r.getMaxMarks() == 100);
        if (!covers0)
            errors.append("• Grading ranges must start from 0\n");
        if (!covers100)
            errors.append("• Grading ranges must go up to 100\n");

        if (errors.length() > 0) {
            validationLabel.setText(errors.toString());
            validationBox.setVisible(true);
            validationBox.setManaged(true);
        } else {
            validationBox.setVisible(false);
            validationBox.setManaged(false);
        }
    }

    private boolean rangesOverlap(double min1, double max1, double min2, double max2) {
        return !(max1 < min2 || max2 < min1);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        validateGrading();
        if (validationBox.isVisible()) {
            util.DialogUtil.showError(dialogStage, "Validation Error",
                    "Please fix the validation errors before saving.");
            return;
        }

        java.util.ResourceBundle messages = SceneManager.getBundle();
        // Confirm with user
        boolean confirmed = util.DialogUtil.showConfirmation(dialogStage,
                messages.getString("settings.grading.confirm.title"),
                messages.getString("settings.grading.confirm.message"));

        if (!confirmed) {
            return;
        }

        gradingDAO.deleteByUniversityAndCategory(currentUser.getUniversityId(), currentCategory);

        for (GradingPolicyRow row : gradingData) {
            gradingDAO.save(new GradingPolicy(
                    currentUser.getUniversityId(),
                    currentCategory,
                    row.getGradeName(),
                    row.getGradePoint(),
                    row.getMinMarks(),
                    row.getMaxMarks()));
        }

        System.out.println(
                "Grading policy saved for " + currentCategory + ". Recalculation will be implemented in Phase 2.5.7");

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public static class GradingPolicyRow {
        private final SimpleStringProperty gradeName;
        private final SimpleDoubleProperty minMarks;
        private final SimpleDoubleProperty maxMarks;
        private final SimpleDoubleProperty gradePoint;
        private int id;

        public GradingPolicyRow(int id, String gradeName, double minMarks, double maxMarks, double gradePoint) {
            this.id = id;
            this.gradeName = new SimpleStringProperty(gradeName);
            this.minMarks = new SimpleDoubleProperty(minMarks);
            this.maxMarks = new SimpleDoubleProperty(maxMarks);
            this.gradePoint = new SimpleDoubleProperty(gradePoint);
        }

        public int getId() {
            return id;
        }

        public String getGradeName() {
            return gradeName.get();
        }

        public void setGradeName(String value) {
            gradeName.set(value);
        }

        public SimpleStringProperty gradeNameProperty() {
            return gradeName;
        }

        public double getMinMarks() {
            return minMarks.get();
        }

        public void setMinMarks(double value) {
            minMarks.set(value);
        }

        public SimpleDoubleProperty minMarksProperty() {
            return minMarks;
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

        public double getGradePoint() {
            return gradePoint.get();
        }

        public void setGradePoint(double value) {
            gradePoint.set(value);
        }

        public SimpleDoubleProperty gradePointProperty() {
            return gradePoint;
        }
    }
}