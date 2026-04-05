package org.SmartEmergencyTrafficControl.service;

import org.SmartEmergencyTrafficControl.model.Location;
import org.SmartEmergencyTrafficControl.model.SignalStatus;
import org.SmartEmergencyTrafficControl.model.TrafficLight;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrafficService {

    private final List<TrafficLight> trafficLights = new ArrayList<>();
    private List<Location> route = new ArrayList<>();
    
    private static final double YELLOW_THRESHOLD = 800.0;
    private static final double GREEN_THRESHOLD = 500.0;
    
    private final Map<String, Double> previousDistances = new HashMap<>();
    
    private long lastLocationTimestamp = 0;

    private final Random random = new Random();

    public TrafficService() {
        addLight("1", "Demo Light 1", 30.271591, 77.048158);
    }

    public void setRoute(List<Location> route) {
        this.route = route;
        System.out.println("Route with " + route.size() + " points has been set.");
    }

    private void addLight(String id, String name, double lat, double lon) {
        TrafficLight light = new TrafficLight(id, name, new Location(lat, lon), getRandomSignalStatus());
        light.setTrafficDensity("LOW");
        initTimer(light);
        trafficLights.add(light);
    }

    private SignalStatus getRandomSignalStatus() {
        SignalStatus[] statuses = {SignalStatus.RED, SignalStatus.YELLOW, SignalStatus.GREEN};
        return statuses[random.nextInt(statuses.length)];
    }

    private void initTimer(TrafficLight light) {
        int maxTimer = getTimerForStatus(light.getStatus());
        // Randomize the starting timer so signals are completely out of sync
        int randomStart = random.nextInt(maxTimer) + 1; 
        light.setTimer(randomStart);
        light.setNormalTimer(randomStart);
        light.setNormalStatus(light.getStatus());
        light.setOverridden(false);
    }

    private int getTimerForStatus(SignalStatus status) {
        switch (status) {
            case RED: return 30;
            case GREEN: return 30;
            case YELLOW: return 5;
            default: return 30;
        }
    }

    public void resetTrafficLights() {
        trafficLights.clear();
        previousDistances.clear();
        route.clear();
        System.out.println("All traffic lights and route cleared.");
    }

    public void addTrafficLightsBatch(List<TrafficLight> newLights) {
        for (TrafficLight light : newLights) {
            if (light.getId() == null) light.setId(UUID.randomUUID().toString());
            
            // Force randomize status (frontend defaults to RED, so we override it here)
            light.setStatus(getRandomSignalStatus());
            
            if (light.getTrafficDensity() == null) light.setTrafficDensity("LOW");
            initTimer(light);
            trafficLights.add(light);
        }
        System.out.println("Added " + newLights.size() + " new traffic lights from route.");
    }

    @Scheduled(fixedRate = 1000)
    public void updateTimers() {
        for (TrafficLight light : trafficLights) {
            int normalTimer = light.getNormalTimer() - 1;
            if (normalTimer <= 0) {
                switch (light.getNormalStatus()) {
                    case GREEN:
                        light.setNormalStatus(SignalStatus.YELLOW);
                        normalTimer = 5;
                        break;
                    case YELLOW:
                        light.setNormalStatus(SignalStatus.RED);
                        normalTimer = 30;
                        break;
                    case RED:
                        light.setNormalStatus(SignalStatus.GREEN);
                        normalTimer = 30;
                        break;
                }
            }
            light.setNormalTimer(normalTimer);

            if (!light.isOverridden()) {
                light.setStatus(light.getNormalStatus());
                light.setTimer(light.getNormalTimer());
            } else {
                light.setTimer(0);
            }
        }
    }

    public void updateAmbulanceLocation(Location ambulanceLocation) {
        if (ambulanceLocation.getTimestamp() > 0) {
            if (ambulanceLocation.getTimestamp() < lastLocationTimestamp) return;
            lastLocationTimestamp = ambulanceLocation.getTimestamp();
        }

        if (route.isEmpty()) {
            return; 
        }

        int closestPointIndex = findClosestPointOnRoute(ambulanceLocation, route);

        for (TrafficLight light : trafficLights) {
            int lightRouteIndex = findClosestPointOnRoute(light.getLocation(), route);
            double distanceFromLightToRoute = calculateDistance(light.getLocation(), route.get(lightRouteIndex));
            
            if (distanceFromLightToRoute > 50 || lightRouteIndex < closestPointIndex) {
                double linearDistance = calculateDistance(ambulanceLocation, light.getLocation());
                if (linearDistance > 50) {
                    restoreNormalState(light, linearDistance);
                    continue;
                }
            }

            double pathDistance = calculateDistanceAlongRoute(closestPointIndex, lightRouteIndex);
            double ambulanceOffset = calculateDistance(ambulanceLocation, route.get(closestPointIndex));
            double effectiveDistance = pathDistance + ambulanceOffset;

            if (effectiveDistance <= YELLOW_THRESHOLD) {
                if (effectiveDistance <= GREEN_THRESHOLD) {
                    if (light.getStatus() != SignalStatus.GREEN) {
                        light.setStatus(SignalStatus.GREEN);
                        light.setOverridden(true);
                        System.out.println("Emergency! Ambulance approaching " + light.getName() + ". Path Distance: " + effectiveDistance + "m -> GREEN");
                    }
                } else {
                    if (light.getStatus() != SignalStatus.YELLOW && light.getStatus() != SignalStatus.GREEN) {
                        light.setStatus(SignalStatus.YELLOW);
                        light.setOverridden(true);
                        System.out.println("Emergency warning! Ambulance approaching " + light.getName() + ". Path Distance: " + effectiveDistance + "m -> YELLOW");
                    }
                }
            } else {
                restoreNormalState(light, effectiveDistance);
            }
        }
    }

    private void restoreNormalState(TrafficLight light, double distance) {
        if (light.isOverridden()) {
            light.setOverridden(false);
            light.setStatus(light.getNormalStatus());
            light.setTimer(light.getNormalTimer());
            System.out.println("Ambulance passed or off route for " + light.getName() + ". Distance: " + distance + "m -> Restored to normal");
        }
    }

    private double calculateDistanceAlongRoute(int startIndex, int endIndex) {
        if (startIndex < 0 || endIndex >= route.size() || startIndex > endIndex) return 0.0;
        double dist = 0.0;
        for (int i = startIndex; i < endIndex; i++) {
            dist += calculateDistance(route.get(i), route.get(i + 1));
        }
        return dist;
    }

    private int findClosestPointOnRoute(Location point, List<Location> routePoints) {
        if (routePoints.isEmpty()) return -1;

        int closestIndex = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < routePoints.size(); i++) {
            double distance = calculateDistance(point, routePoints.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    public List<TrafficLight> getAllTrafficLights() {
        return trafficLights;
    }

    public SignalStatus getSignalStatus() {
        return trafficLights.isEmpty() ? SignalStatus.RED : trafficLights.get(0).getStatus();
    }

    public TrafficLight addTrafficLight(TrafficLight light) {
        if (light.getId() == null) light.setId(UUID.randomUUID().toString());
        light.setStatus(getRandomSignalStatus());
        if (light.getTrafficDensity() == null) light.setTrafficDensity("LOW");
        initTimer(light);
        trafficLights.add(light);
        return light;
    }

    public void setTrafficDensity(String density) {
        for (TrafficLight light : trafficLights) {
            light.setTrafficDensity(density);
        }
        System.out.println("Traffic Density set to " + density + " for all lights.");
    }

    public void setTrafficDensityForLight(String lightId, String density) {
        for (TrafficLight light : trafficLights) {
            if (light.getId().equals(lightId)) {
                light.setTrafficDensity(density);
                System.out.println("Traffic Density set to " + density + " for " + light.getName());
                return;
            }
        }
    }

    public void randomizeTrafficDensities() {
        String[] densities = {"LOW", "MEDIUM", "HIGH"};
        for (TrafficLight light : trafficLights) {
            String density = densities[random.nextInt(densities.length)];
            light.setTrafficDensity(density);
        }
    }

    private double calculateDistance(Location loc1, Location loc2) {
        final int R = 6371; 
        double latDistance = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double lonDistance = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c * 1000;
    }
}