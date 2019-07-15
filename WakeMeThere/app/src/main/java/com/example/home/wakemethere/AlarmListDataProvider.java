package com.example.home.wakemethere;

public class AlarmListDataProvider {
    private String alarm_name_Resource;
    private int id;
    private String name;
    private String latlng;
    private boolean isEnabled;
    private int distance;
    private String message;
    public AlarmListDataProvider(String alarm_name_Resource, int id, String name, int distance, boolean isEnabled, String message) {
        this.alarm_name_Resource = alarm_name_Resource;
        this.id = id;
        this.name = name;
        this.latlng = latlng;
        this.distance = distance;
        this.isEnabled = isEnabled;
        this.message = message;

    }


    public String getAlarm_name_Resource() {
        return this.alarm_name_Resource;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getLatlng() {
        return latlng;
    }

    public int getDistance() {
        return distance;
    }

    public String getMessage() {
        return message;
    }
}
