package ui;

import java.util.List;

import dao.SemesterDAO;
import dao.SubjectDAO;
import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Semester;
import model.Subject;
import model.UserProfile;
import util.ResultCalculator;

public class SemesterResultController {

    @FXML
    private Button backButton;

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label semesterNameLabel;

    @FXML
    private Label gpaLabel;

    @FXML
    private Label gradeClassLabel;

    @FXML
    private VBox subjectRowsContainer;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label totalQualityPointsLabel;

    @FXML
    private Label footerCreditsLabel;

    @FXML
    private Label footerQualityPointsLabel;

    @FXML
    private Label footerGPALabel;

    private Semester currentSemester;
    private UserProfile currentUser;

    private SemesterDAO semesterDAO;
    private SubjectDAO subjectDAO;
    private UserProfileDAO userDAO;
    private dao.UniversityDAO universityDAO;

    @FXML
    public void initialize() {
        semesterDAO = new SemesterDAO();
        subjectDAO = new SubjectDAO();
        userDAO = new UserProfileDAO();
        universityDAO = new dao.UniversityDAO();

        // Load user
        currentUser = userDAO.getUser();
    }

    public void setSemester(Semester semester) {
        this.currentSemester = semester;
        loadResultData();
    }

    private void loadResultData() {
        if (currentSemester == null || currentUser == null)
            return;

        // Student Info
        studentNameLabel.setText(currentUser.getName());

        // Get university name
        String universityName = "Unknown University";
        model.University university = universityDAO.getById(currentUser.getUniversityId());
        if (university != null) {
            universityName = university.getName();
        }

        departmentLabel.setText(currentUser.getDepartment() + " - " + universityName);

        // Semester Info
        semesterNameLabel.setText(currentSemester.getSemesterName());

        // GPA
        Double gpa = currentSemester.getGpa();
        if (gpa != null) {
            gpaLabel.setText(String.format("%.2f", gpa));
            footerGPALabel.setText(String.format("%.2f", gpa));

            // Grade Classification
            String gradeClass = ResultCalculator.getGradeClass(gpa);
            gradeClassLabel.setText(gradeClass);

            // Color based on performance
            if (gpa >= 3.7) {
                gradeClassLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else if (gpa >= 3.0) {
                gradeClassLabel.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else if (gpa >= 2.0) {
                gradeClassLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 12px; -fx-font-weight: bold;");
            } else {
                gradeClassLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        } else {
            gpaLabel.setText("N/A");
            footerGPALabel.setText("N/A");
            gradeClassLabel.setText("Not Calculated");
        }

        // Load Subjects
        loadSubjects();
    }

    private void loadSubjects() {
        subjectRowsContainer.getChildren().clear();

        List<Subject> subjects = subjectDAO.getAllBySemester(currentSemester.getId());

        int totalCredits = 0;
        double totalQualityPoints = 0.0;

        for (Subject subject : subjects) {
            // Create row for each subject
            HBox row = createSubjectRow(subject);
            subjectRowsContainer.getChildren().add(row);

            // Calculate totals
            int subjectCredits = subject.getTheoryCreditHours();
            if (subject.isHasPractical() && subject.getPracticalCreditHours() != null) {
                subjectCredits += subject.getPracticalCreditHours();
            }

            totalCredits += subjectCredits;

            // Quality Points = Grade Point × Credit Hours
            if (subject.getTheoryGradePoint() != null) {
                totalQualityPoints += subject.getTheoryGradePoint() * subject.getTheoryCreditHours();
            }

            if (subject.isHasPractical() && subject.getPracticalGradePoint() != null
                    && subject.getPracticalCreditHours() != null) {
                totalQualityPoints += subject.getPracticalGradePoint() * subject.getPracticalCreditHours();
            }
        }

        // Update totals
        totalCreditsLabel.setText(String.valueOf(totalCredits));
        totalQualityPointsLabel.setText(String.format("%.2f", totalQualityPoints));

        footerCreditsLabel.setText(String.valueOf(totalCredits));
        footerQualityPointsLabel.setText(String.format("%.2f", totalQualityPoints));
    }

    private HBox createSubjectRow(Subject subject) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10 0;");

        // Subject Name
        Label nameLabel = new Label(subject.getSubjectName());
        nameLabel.setPrefWidth(200);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Credits
        int credits = subject.getTheoryCreditHours();
        if (subject.isHasPractical() && subject.getPracticalCreditHours() != null) {
            credits += subject.getPracticalCreditHours();
        }
        Label creditsLabel = new Label(String.valueOf(credits));
        creditsLabel.setPrefWidth(60);
        creditsLabel.setAlignment(Pos.CENTER);
        creditsLabel.setStyle("-fx-text-fill: #BDB7E2; -fx-font-size: 14px;");

        // Theory Grade
        String theoryGrade = subject.getTheoryGrade() != null ? subject.getTheoryGrade() : "-";
        Label theoryLabel = new Label(theoryGrade);
        theoryLabel.setPrefWidth(80);
        theoryLabel.setAlignment(Pos.CENTER);
        theoryLabel.setStyle("-fx-text-fill: #A78BFA; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Practical Grade
        String practicalGrade = "-";
        if (subject.isHasPractical()) {
            practicalGrade = subject.getPracticalGrade() != null ? subject.getPracticalGrade() : "-";
        }
        Label practicalLabel = new Label(practicalGrade);
        practicalLabel.setPrefWidth(80);
        practicalLabel.setAlignment(Pos.CENTER);
        practicalLabel.setStyle("-fx-text-fill: #A78BFA; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Overall Grade (better of theory/practical or average)
        String overallGrade = theoryGrade;
        if (subject.isHasPractical() && !practicalGrade.equals("-")) {
            // For display, we can show theory grade or combine logic
            // For now, show theory as "overall"
            overallGrade = theoryGrade;
        }
        Label gradeLabel = new Label(overallGrade);
        gradeLabel.setPrefWidth(60);
        gradeLabel.setAlignment(Pos.CENTER);
        gradeLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Grade Points (Quality Points for this subject)
        double qualityPoints = 0.0;
        if (subject.getTheoryGradePoint() != null) {
            qualityPoints += subject.getTheoryGradePoint() * subject.getTheoryCreditHours();
        }
        if (subject.isHasPractical() && subject.getPracticalGradePoint() != null
                && subject.getPracticalCreditHours() != null) {
            qualityPoints += subject.getPracticalGradePoint() * subject.getPracticalCreditHours();
        }

        Label pointsLabel = new Label(String.format("%.2f", qualityPoints));
        pointsLabel.setPrefWidth(60);
        pointsLabel.setAlignment(Pos.CENTER);
        pointsLabel.setStyle("-fx-text-fill: #BDB7E2; -fx-font-size: 14px;");

        row.getChildren().addAll(nameLabel, spacer, creditsLabel, theoryLabel, practicalLabel, gradeLabel,
                pointsLabel);

        return row;
    }

    @FXML
    private void handleBack() {
        SceneManager.loadCenter("Dashboard.fxml");
    }
}