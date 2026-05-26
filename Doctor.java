package com.example.tatwa10.ModelClass;

public class Doctor {

    private int id;
    private String staffId;
    private String role;
    private String fullName;
    private String passwordHash;
    private String specialization;
    private String phone;
    private String imageUrl;
    private float rating;
    private int price;

    public int getPrice() {
        return price;
    }
    public float getRating() {
        return rating;
    }

    public Doctor() {
    }

    public int getId() {
        return id;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // setters (optional but safe)
    public void setId(int id) {
        this.id = id;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}