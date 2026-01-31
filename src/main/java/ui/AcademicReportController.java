package ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class AcademicReportController {

    @FXML
    private Button backButton;

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label cgpaLabel;

    @FXML
    private Label cgpaClassLabel;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label semesterCountLabel;

    @FXML
    private Label bestSemesterNameLabel;

    @FXML
    private Label bestSemesterGPALabel;

    @FXML
    private Label avgGPALabel;

    @FXML
    private Label completedSemestersLabel;

    @FXML
    private VBox semesterRowsContainer;

    @FXML
    private Label gradeALabel;

    @FXML
    private Label gradeBLabel;

    @FXML
    private Label gradeCLabel;

    @FXML
    private Label gradeDFLabel;

    private UserProfile currentUser;
    private SemesterDAO semesterDAO;
    private SubjectDAO subjectDAO;
    private UserProfileDAO userDAO;

    @FXML
    public void initialize() {
        semesterDAO = new SemesterDAO();
        subjectDAO = new SubjectDAO();
        userDAO = new UserProfileDAO();

        currentUser = userDAO.getUser();

        loadAcademicReport();
    }

    private void loadAcademicReport() {
        if (currentUser == null)
            return;

        java.util.ResourceBundle messages = SceneManager.getBundle();

        // Student Info
        studentNameLabel.setText(currentUser.getName());

        // Get university name
        String universityName = messages.getString("semester.label.university.unknown");
        dao.UniversityDAO universityDAO = new dao.UniversityDAO();
        model.University university = universityDAO.getById(currentUser.getUniversityId());
        if (university != null) {
            universityName = university.getName();
        }

        departmentLabel.setText(currentUser.getDepartment() + " - " + universityName);

        // Load all semesters
        List<Semester> semesters = semesterDAO.getAllByUser(currentUser.getId());

        // Calculate CGPA
        Double cgpa = semesterDAO.getCGPA(currentUser.getId());
        if (cgpa != null) {
            cgpaLabel.setText(String.format("%.2f", cgpa));

            String gradeClass = ResultCalculator.getGradeClass(cgpa);
            if ("First Class".equals(gradeClass)) {
                cgpaClassLabel.setText(messages.getString("report.label.excellent"));
            } else if ("Second Class".equals(gradeClass)) {
                cgpaClassLabel.setText(messages.getString("report.label.good"));
            } else {
                cgpaClassLabel.setText(messages.getString("report.label.fair"));
            }

            // Color based on performance
            if (cgpa >= 3.7) {
                cgpaClassLabel.setStyle("-fx-text-fill: #10B981; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else if (cgpa >= 3.0) {
                cgpaClassLabel.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else if (cgpa >= 2.0) {
                cgpaClassLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-size: 14px; -fx-font-weight: bold;");
            } else {
                cgpaClassLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 14px; -fx-font-weight: bold;");
            }
        } else {
            cgpaLabel.setText(messages.getString("dashboard.gpa.na"));
            cgpaClassLabel.setText(messages.getString("report.label.na"));
        }

        // Total Credits
        int totalCredits = semesterDAO.getTotalCreditHoursCompleted(currentUser.getId());
        totalCreditsLabel.setText(String.valueOf(totalCredits));

        // Semester Count
        semesterCountLabel.setText(
                semesters.size() + " " + (semesters.size() != 1 ? messages.getString("report.table.semester") + "s"
                        : messages.getString("report.table.semester")));

        // Calculate statistics
        calculateStatistics(semesters);

        // Load semester rows
        loadSemesterRows(semesters);

        // Calculate grade distribution
        calculateGradeDistribution(semesters);
    }

    private void calculateStatistics(List<Semester> semesters) {
        java.util.ResourceBundle messages = SceneManager.getBundle();
        if (semesters.isEmpty()) {
            bestSemesterNameLabel.setText(messages.getString("dashboard.gpa.na"));
            bestSemesterGPALabel
                    .setText(messages.getString("semester.label.gpa") + " " + messages.getString("dashboard.gpa.na"));
            avgGPALabel.setText(messages.getString("dashboard.gpa.na"));
            completedSemestersLabel.setText("0/0");
            return;
        }

        // Find best semester
        Semester bestSemester = null;
        double bestGPA = 0.0;

        int completedCount = 0;
        double totalGPA = 0.0;
        int gpaCount = 0;

        for (Semester semester : semesters) {
            if (semester.getGpa() != null) {
                completedCount++;
                totalGPA += semester.getGpa();
                gpaCount++;

                if (semester.getGpa() > bestGPA) {
                    bestGPA = semester.getGpa();
                    bestSemester = semester;
                }
            }
        }

        // Best Semester
        if (bestSemester != null) {
            bestSemesterNameLabel.setText(bestSemester.getSemesterName());
            bestSemesterGPALabel.setText(java.text.MessageFormat.format("{0} {1,number,0.00}",
                    messages.getString("semester.label.gpa"), bestGPA));
        } else {
            bestSemesterNameLabel.setText(messages.getString("dashboard.gpa.na"));
            bestSemesterGPALabel
                    .setText(messages.getString("semester.label.gpa") + " " + messages.getString("dashboard.gpa.na"));
        }

        // Average GPA
        if (gpaCount > 0) {
            double avgGPA = totalGPA / gpaCount;
            avgGPALabel.setText(String.format("%.2f", avgGPA));
        } else {
            avgGPALabel.setText(messages.getString("dashboard.gpa.na"));
        }

        // Completed Semesters
        completedSemestersLabel.setText(completedCount + "/" + semesters.size());
    }

    private void loadSemesterRows(List<Semester> semesters) {
        semesterRowsContainer.getChildren().clear();

        for (Semester semester : semesters) {
            HBox row = createSemesterRow(semester);
            semesterRowsContainer.getChildren().add(row);
        }
    }

    private HBox createSemesterRow(Semester semester) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 10 0;");

        // Semester Name
        Label nameLabel = new Label(semester.getSemesterName());
        nameLabel.setPrefWidth(180);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Credits
        int credits = subjectDAO.getTotalCreditHours(semester.getId());
        Label creditsLabel = new Label(String.valueOf(credits));
        creditsLabel.setPrefWidth(80);
        creditsLabel.setAlignment(Pos.CENTER);
        creditsLabel.setStyle("-fx-text-fill: #BDB7E2; -fx-font-size: 14px;");

        // GPA
        java.util.ResourceBundle messages = SceneManager.getBundle();
        String gpaText = semester.getGpa() != null ? String.format("%.2f", semester.getGpa())
                : messages.getString("dashboard.gpa.na");
        Label gpaLabel = new Label(gpaText);
        gpaLabel.setPrefWidth(80);
        gpaLabel.setAlignment(Pos.CENTER);
        gpaLabel.setStyle("-fx-text-fill: #A78BFA; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Status
        String status = semester.getGpa() != null ? "✓ " + messages.getString("report.status.completed")
                : "⏳ " + messages.getString("report.status.in_progress");
        String statusColor = semester.getGpa() != null ? "#10B981" : "#F59E0B";
        Label statusLabel = new Label(status);
        statusLabel.setPrefWidth(120);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-size: 13px; -fx-font-weight: bold;");

        // View Button
        Button viewButton = new Button(messages.getString("dashboard.btn.view_subjects")); // Or "View Details" if I add
                                                                                           // a key
        viewButton.setPrefWidth(100);
        viewButton.setStyle(
                "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 12;");
        viewButton.setOnAction(e -> handleViewSemester(semester));

        row.getChildren().addAll(nameLabel, spacer, creditsLabel, gpaLabel, statusLabel, viewButton);

        return row;
    }

    private void calculateGradeDistribution(List<Semester> semesters) {
        Map<String, Integer> gradeCounts = new HashMap<>();
        gradeCounts.put("A", 0);
        gradeCounts.put("B", 0);
        gradeCounts.put("C", 0);
        gradeCounts.put("D", 0);
        gradeCounts.put("F", 0);

        for (Semester semester : semesters) {
            List<Subject> subjects = subjectDAO.getAllBySemester(semester.getId());

            for (Subject subject : subjects) {
                // Count theory grade
                if (subject.getTheoryGrade() != null) {
                    String grade = subject.getTheoryGrade();
                    if (grade.startsWith("A")) {
                        gradeCounts.put("A", gradeCounts.get("A") + 1);
                    } else if (grade.startsWith("B")) {
                        gradeCounts.put("B", gradeCounts.get("B") + 1);
                    } else if (grade.startsWith("C")) {
                        gradeCounts.put("C", gradeCounts.get("C") + 1);
                    } else if (grade.equals("D")) {
                        gradeCounts.put("D", gradeCounts.get("D") + 1);
                    } else if (grade.equals("F")) {
                        gradeCounts.put("F", gradeCounts.get("F") + 1);
                    }
                }

                // Count practical grade
                if (subject.isHasPractical() && subject.getPracticalGrade() != null) {
                    String grade = subject.getPracticalGrade();
                    if (grade.startsWith("A")) {
                        gradeCounts.put("A", gradeCounts.get("A") + 1);
                    } else if (grade.startsWith("B")) {
                        gradeCounts.put("B", gradeCounts.get("B") + 1);
                    } else if (grade.startsWith("C")) {
                        gradeCounts.put("C", gradeCounts.get("C") + 1);
                    } else if (grade.equals("D")) {
                        gradeCounts.put("D", gradeCounts.get("D") + 1);
                    } else if (grade.equals("F")) {
                        gradeCounts.put("F", gradeCounts.get("F") + 1);
                    }
                }
            }
        }

        // Update labels
        gradeALabel.setText(String.valueOf(gradeCounts.get("A")));
        gradeBLabel.setText(String.valueOf(gradeCounts.get("B")));
        gradeCLabel.setText(String.valueOf(gradeCounts.get("C")));
        gradeDFLabel.setText(String.valueOf(gradeCounts.get("D") + gradeCounts.get("F")));
    }

    private void handleViewSemester(Semester semester) {
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/SemesterDetail.fxml");
            javafx.scene.Parent root = loader.load();

            SemesterDetailController controller = loader.getController();
            controller.setSemester(semester);

            SceneManager.getRootLayout().setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
            java.util.ResourceBundle messages = SceneManager.getBundle();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open semester details: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.loadCenter("Dashboard.fxml");
    }
}