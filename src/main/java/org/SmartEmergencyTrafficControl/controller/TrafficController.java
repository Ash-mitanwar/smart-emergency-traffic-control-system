package org.SmartEmergencyTrafficControl.controller;

import org.SmartEmergencyTrafficControl.model.Location;
import org.SmartEmergencyTrafficControl.model.SignalStatus;
import org.SmartEmergencyTrafficControl.model.TrafficLight;
import org.SmartEmergencyTrafficControl.service.TrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    // --- Existing APIs ---

    @PostMapping("/ambulance/location")
    public void updateAmbulanceLocation(@RequestBody Location location) {
        trafficService.updateAmbulanceLocation(location);
    }

    @GetMapping("/signal/status")
    public SignalStatus getSignalStatus() {
        return trafficService.getSignalStatus();
    }

    @GetMapping("/traffic-lights")
    public List<TrafficLight> getAllTrafficLights() {
        return trafficService.getAllTrafficLights();
    }

    @PostMapping("/traffic-lights")
    public TrafficLight addTrafficLight(@RequestBody TrafficLight light) {
        return trafficService.addTrafficLight(light);
    }

    @PostMapping("/traffic/config")
    public void setTrafficDensity(@RequestBody Map<String, String> config) {
        String density = config.get("density");
        trafficService.setTrafficDensity(density);
    }
    
    @PostMapping("/traffic/config/{lightId}")
    public void setTrafficDensityForLight(@PathVariable String lightId, @RequestBody Map<String, String> config) {
        String density = config.get("density");
        trafficService.setTrafficDensityForLight(lightId, density);
    }
    
    @PostMapping("/traffic/simulate-sensors")
    public void simulateSensors() {
        trafficService.randomizeTrafficDensities();
    }

    // --- New APIs for Dynamic Route ---

    // Clear all existing traffic lights
    @PostMapping("/traffic-lights/reset")
    public void resetTrafficLights() {
        trafficService.resetTrafficLights();
    }

    // Add a batch of traffic lights (from frontend detection)
    @PostMapping("/traffic-lights/batch")
    public void addTrafficLightsBatch(@RequestBody List<TrafficLight> lights) {
        trafficService.addTrafficLightsBatch(lights);
    }

    // Set the full route for path-based distance calculation
    @PostMapping("/route")
    public void setRoute(@RequestBody List<Location> routePoints) {
        trafficService.setRoute(routePoints);
    }
}