package model;

public class UserProfile {

    private int id;
    private String name;
    private String department;
    private int universityId;

    public UserProfile(int id, String name, String department, int universityId) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.universityId = universityId;
    }
    
    public UserProfile() {
        
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

     public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getUniversityId() {
        return universityId;
    }
}