package com.example.krushiler.testbase;

public class PlayerStats {
    private String place;
    private String name;
    private String misses;
    private String time;

    public PlayerStats(String place, String name, String misses, String time) {
        this.place = place;
        this.name = name;
        this.misses = misses;
        this.time = time;
    }
    public String getPlace() {
        return place;
    }

    public String getName() {
        return name;
    }

    public String getMisses() {
        return misses;
    }

    public String getTime() {
        return time;
    }
}