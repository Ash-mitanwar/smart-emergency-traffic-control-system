package org.SmartEmergencyTrafficControl.model;

public class TrafficLight {
    private String id;
    private String name;
    private Location location;
    private SignalStatus status;
    private String trafficDensity; // "HIGH", "MEDIUM", "LOW"
    private int timer;
    private boolean isOverridden; // true if emergency vehicle is nearby
    
    // Normal cycle state
    private SignalStatus normalStatus;
    private int normalTimer;

    public TrafficLight() {
        this.trafficDensity = "LOW"; // Default
        this.timer = 30; // 30 seconds default for RED/GREEN
        this.normalStatus = SignalStatus.RED;
        this.normalTimer = 30;
    }

    public TrafficLight(String id, String name, Location location, SignalStatus status) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.status = status;
        this.normalStatus = status;
        this.trafficDensity = "LOW";
        this.timer = 30;
        this.normalTimer = 30;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public SignalStatus getStatus() { return status; }
    public void setStatus(SignalStatus status) { this.status = status; }

    public String getTrafficDensity() { return trafficDensity; }
    public void setTrafficDensity(String trafficDensity) { this.trafficDensity = trafficDensity; }

    public int getTimer() { return timer; }
    public void setTimer(int timer) { this.timer = timer; }

    public boolean isOverridden() { return isOverridden; }
    public void setOverridden(boolean overridden) { isOverridden = overridden; }

    public SignalStatus getNormalStatus() { return normalStatus; }
    public void setNormalStatus(SignalStatus normalStatus) { this.normalStatus = normalStatus; }

    public int getNormalTimer() { return normalTimer; }
    public void setNormalTimer(int normalTimer) { this.normalTimer = normalTimer; }
}