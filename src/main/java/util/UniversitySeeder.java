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

        // Check if universities already exist
        if (universityDAO.count() > 0) {
            System.out.println("✅ Universities already seeded. Skipping...");
            return;
        }

        System.out.println("🌱 Seeding universities...");

        // Seed Sukkur IBA University
        seedSukkurIBA(universityDAO);

        System.out.println("✅ University seeding complete!");
    }

    private static void seedSukkurIBA(UniversityDAO universityDAO) {
        // Create university
        University sukkurIBA = new University();
        sukkurIBA.setName("Sukkur IBA University");
        int universityId = universityDAO.save(sukkurIBA);

        if (universityId == -1) {
            System.err.println("❌ Failed to save Sukkur IBA University");
            return;
        }

        System.out.println("✅ Sukkur IBA University created with ID: " + universityId);

        // Seed grading policies
        seedSukkurIBAGradingPolicy(universityId);

        // Seed assessment policies
        seedSukkurIBAAssessmentPolicy(universityId);
    }

    private static void seedSukkurIBAGradingPolicy(int universityId) {
        GradingPolicyDAO gradingDAO = new GradingPolicyDAO();
        List<GradingPolicy> policies = new ArrayList<>();

        // ==========================================
        // THEORY GRADING POLICY (100 marks)
        // ==========================================
        policies.add(new GradingPolicy(universityId, "Theory", "A", 4.00, 93, 100));
        policies.add(new GradingPolicy(universityId, "Theory", "A-", 3.67, 87, 92));
        policies.add(new GradingPolicy(universityId, "Theory", "B+", 3.33, 82, 86));
        policies.add(new GradingPolicy(universityId, "Theory", "B", 3.00, 77, 81));
        policies.add(new GradingPolicy(universityId, "Theory", "B-", 2.67, 72, 76));
        policies.add(new GradingPolicy(universityId, "Theory", "C+", 2.30, 68, 71));
        policies.add(new GradingPolicy(universityId, "Theory", "C", 2.00, 64, 67));
        policies.add(new GradingPolicy(universityId, "Theory", "C-", 1.67, 60, 63));
        policies.add(new GradingPolicy(universityId, "Theory", "F", 0.00, 0, 59));

        // ==========================================
        // PRACTICAL GRADING POLICY (Standard Percentage)
        // ==========================================
        policies.add(new GradingPolicy(universityId, "Practical", "A", 4.00, 93, 100));
        policies.add(new GradingPolicy(universityId, "Practical", "A-", 3.67, 87, 92));
        policies.add(new GradingPolicy(universityId, "Practical", "B+", 3.33, 82, 86));
        policies.add(new GradingPolicy(universityId, "Practical", "B", 3.00, 77, 81));
        policies.add(new GradingPolicy(universityId, "Practical", "B-", 2.67, 72, 76));
        policies.add(new GradingPolicy(universityId, "Practical", "C+", 2.30, 68, 71));
        policies.add(new GradingPolicy(universityId, "Practical", "C", 2.00, 64, 67));
        policies.add(new GradingPolicy(universityId, "Practical", "C-", 1.67, 60, 63));
        policies.add(new GradingPolicy(universityId, "Practical", "F", 0.00, 0, 59));

        // Save all policies
        gradingDAO.saveAll(policies);
        System.out.println("✅ Sukkur IBA grading policies seeded (Theory + Practical)");
    }

    private static void seedSukkurIBAAssessmentPolicy(int universityId) {
        AssessmentPolicyDAO assessmentDAO = new AssessmentPolicyDAO();
        List<AssessmentPolicy> policies = new ArrayList<>();

        // ==========================================
        // THEORY ASSESSMENT POLICY (Total = 100)
        // ==========================================
        policies.add(new AssessmentPolicy(universityId, "Theory", "Mid", 30));
        policies.add(new AssessmentPolicy(universityId, "Theory", "Final", 50));
        policies.add(new AssessmentPolicy(universityId, "Theory", "Sessional", 20));

        // ==========================================
        // PRACTICAL ASSESSMENT POLICY (Total = 50)
        // ==========================================
        policies.add(new AssessmentPolicy(universityId, "Practical", "Mid Lab", 15));
        policies.add(new AssessmentPolicy(universityId, "Practical", "Final Lab", 7));
        policies.add(new AssessmentPolicy(universityId, "Practical", "External Viva", 18));
        policies.add(new AssessmentPolicy(universityId, "Practical", "Sessional", 10));

        // Save all policies
        assessmentDAO.saveAll(policies);
        System.out.println("✅ Sukkur IBA assessment policies seeded (Theory + Practical)");
    }
}