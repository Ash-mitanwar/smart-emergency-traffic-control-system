package org.SmartEmergencyTrafficControl.controller;

import org.SmartEmergencyTrafficControl.model.Location;
import org.SmartEmergencyTrafficControl.service.TrafficService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private TrafficService trafficService;

    @PostMapping
    public void setRoute(@RequestBody List<Location> route) {
        trafficService.setRoute(route);
    }
}