package model;

public class University {

    private int id;
    private String name;

    // Constructor with all fields
    public University(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // No-arg constructor
    public University() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override toString for ComboBox display
    @Override
    public String toString() {
        return name;  // Display university name in ComboBox
    }
}