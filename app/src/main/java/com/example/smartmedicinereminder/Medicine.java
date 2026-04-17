package com.example.smartmedicinereminder;

public class Medicine {

    private int id;
    private String name;
    private String dosage;
    private String time;
    private String date; // ✅ NEW
    private int status;  // 0 = not taken, 1 = taken

    // ✅ UPDATED CONSTRUCTOR
    public Medicine(int id, String name, String dosage, String time, String date, int status) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.date = date; // ✅ NEW
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDosage() {
        return dosage;
    }

    public String getTime() {
        return time;
    }

    // ✅ NEW GETTER
    public String getDate() {
        return date;
    }

    // ✅ STATUS METHODS
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}