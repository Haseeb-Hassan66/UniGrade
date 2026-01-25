package util;

import dao.AssessmentPolicyDAO;
import dao.GradingPolicyDAO;
import dao.UniversityDAO;
import model.AssessmentPolicy;
import model.GradingPolicy;
import model.University;

import java.util.ArrayList;
import java.util.List;

public class UniversitySeeder {

    public static void seedUniversities() {
        UniversityDAO universityDAO = new UniversityDAO();

        System.out.println("Checking university templates...");

        seedSukkurIBA(universityDAO);
        seedGeneralUniversity(universityDAO);

        System.out.println("University template check complete.");
    }

    private static void seedSukkurIBA(UniversityDAO universityDAO) {
        if (universityDAO.exists("Sukkur IBA University")) {
            System.out.println("Sukkur IBA already exists. Skipping...");
            return;
        }

        University sukkurIBA = new University();
        sukkurIBA.setName("Sukkur IBA University");
        int universityId = universityDAO.save(sukkurIBA);

        if (universityId == -1)
            return;

        seedSukkurIBAGradingPolicy(universityId);
        seedSukkurIBAAssessmentPolicy(universityId);
    }

    private static void seedGeneralUniversity(UniversityDAO universityDAO) {

        if (universityDAO.exists("General/Standard (Customizable)")) {
            System.out.println("General/Standard already exists. Skipping...");
            return;
        }

        University general = new University();
        general.setName("General/Standard (Customizable)");
        int universityId = universityDAO.save(general);

        if (universityId == -1)
            return;

        seedGeneralGradingPolicy(universityId);
        seedGeneralAssessmentPolicy(universityId);
    }

    private static void seedSukkurIBAGradingPolicy(int universityId) {
        GradingPolicyDAO gradingDAO = new GradingPolicyDAO();
        List<GradingPolicy> policies = new ArrayList<>();

        policies.add(new GradingPolicy(universityId, "Theory", "A", 4.00, 93, 100));
        policies.add(new GradingPolicy(universityId, "Theory", "A-", 3.67, 87, 92));
        policies.add(new GradingPolicy(universityId, "Theory", "B+", 3.33, 82, 86));
        policies.add(new GradingPolicy(universityId, "Theory", "B", 3.00, 77, 81));
        policies.add(new GradingPolicy(universityId, "Theory", "B-", 2.67, 72, 76));
        policies.add(new GradingPolicy(universityId, "Theory", "C+", 2.30, 68, 71));
        policies.add(new GradingPolicy(universityId, "Theory", "C", 2.00, 64, 67));
        policies.add(new GradingPolicy(universityId, "Theory", "C-", 1.67, 60, 63));
        policies.add(new GradingPolicy(universityId, "Theory", "F", 0.00, 0, 59));

        policies.add(new GradingPolicy(universityId, "Practical", "A", 4.00, 93, 100));
        policies.add(new GradingPolicy(universityId, "Practical", "A-", 3.67, 87, 92));
        policies.add(new GradingPolicy(universityId, "Practical", "B+", 3.33, 82, 86));
        policies.add(new GradingPolicy(universityId, "Practical", "B", 3.00, 77, 81));
        policies.add(new GradingPolicy(universityId, "Practical", "B-", 2.67, 72, 76));
        policies.add(new GradingPolicy(universityId, "Practical", "C+", 2.30, 68, 71));
        policies.add(new GradingPolicy(universityId, "Practical", "C", 2.00, 64, 67));
        policies.add(new GradingPolicy(universityId, "Practical", "C-", 1.67, 60, 63));
        policies.add(new GradingPolicy(universityId, "Practical", "F", 0.00, 0, 59));

        gradingDAO.saveAll(policies);
    }

    private static void seedSukkurIBAAssessmentPolicy(int universityId) {
        AssessmentPolicyDAO assessmentDAO = new AssessmentPolicyDAO();
        List<AssessmentPolicy> policies = new ArrayList<>();

        policies.add(new AssessmentPolicy(universityId, "Theory", "Mid", 30));
        policies.add(new AssessmentPolicy(universityId, "Theory", "Final", 50));
        policies.add(new AssessmentPolicy(universityId, "Theory", "Sessional", 20));

        policies.add(new AssessmentPolicy(universityId, "Practical", "Mid Lab", 15));
        policies.add(new AssessmentPolicy(universityId, "Practical", "Final Lab", 7));
        policies.add(new AssessmentPolicy(universityId, "Practical", "External Viva", 18));
        policies.add(new AssessmentPolicy(universityId, "Practical", "Sessional", 10));

        assessmentDAO.saveAll(policies);
    }

    private static void seedGeneralGradingPolicy(int universityId) {
        GradingPolicyDAO gradingDAO = new GradingPolicyDAO();
        List<GradingPolicy> policies = new ArrayList<>();

        policies.add(new GradingPolicy(universityId, "Theory", "A", 4.00, 85, 100));
        policies.add(new GradingPolicy(universityId, "Theory", "A-", 3.67, 80, 84));
        policies.add(new GradingPolicy(universityId, "Theory", "B+", 3.33, 75, 79));
        policies.add(new GradingPolicy(universityId, "Theory", "B", 3.00, 70, 74));
        policies.add(new GradingPolicy(universityId, "Theory", "B-", 2.67, 65, 69));
        policies.add(new GradingPolicy(universityId, "Theory", "C+", 2.30, 61, 64));
        policies.add(new GradingPolicy(universityId, "Theory", "C", 2.00, 58, 60));
        policies.add(new GradingPolicy(universityId, "Theory", "C-", 1.67, 55, 57));
        policies.add(new GradingPolicy(universityId, "Theory", "F", 0.00, 0, 54));

        policies.add(new GradingPolicy(universityId, "Practical", "A", 4.00, 90, 100));
        policies.add(new GradingPolicy(universityId, "Practical", "A-", 3.67, 85, 89));
        policies.add(new GradingPolicy(universityId, "Practical", "B+", 3.33, 80, 84));
        policies.add(new GradingPolicy(universityId, "Practical", "B", 3.00, 75, 79));
        policies.add(new GradingPolicy(universityId, "Practical", "B-", 2.67, 70, 74));
        policies.add(new GradingPolicy(universityId, "Practical", "C+", 2.30, 65, 69));
        policies.add(new GradingPolicy(universityId, "Practical", "C", 2.00, 60, 64));
        policies.add(new GradingPolicy(universityId, "Practical", "C-", 1.67, 55, 59));
        policies.add(new GradingPolicy(universityId, "Practical", "F", 0.00, 0, 54));

        gradingDAO.saveAll(policies);
    }

    private static void seedGeneralAssessmentPolicy(int universityId) {
        AssessmentPolicyDAO assessmentDAO = new AssessmentPolicyDAO();
        List<AssessmentPolicy> policies = new ArrayList<>();

        policies.add(new AssessmentPolicy(universityId, "Theory", "Mid", 30));
        policies.add(new AssessmentPolicy(universityId, "Theory", "Final", 50));
        policies.add(new AssessmentPolicy(universityId, "Theory", "Sessional", 20));

        policies.add(new AssessmentPolicy(universityId, "Practical", "Mid", 15));
        policies.add(new AssessmentPolicy(universityId, "Practical", "Final", 25));
        policies.add(new AssessmentPolicy(universityId, "Practical", "Sessional", 10));

        assessmentDAO.saveAll(policies);
    }
}