# Smart Emergency Traffic Control System

![Deployment Status](https://img.shields.io/badge/Deployed-Live-success)
**Live Demo:** [https://smart-emergency-traffic-control-system.onrender.com](https://smart-emergency-traffic-control-system.onrender.com)

This project implements a Smart Emergency Traffic Control System using Spring Boot for the backend and a modern HTML/JS Map interface for the frontend.

## Features
- **Live Deployment:** Accessible anywhere via Render.
- **Role-Based Authentication:** Dedicated login portal for Police, Fire, and Ambulance vehicles.
- **Dynamic Routing:** Map-based interface (Leaflet & OSRM) allowing users to select any start and destination points.
- **Real-World Signal Detection:** Automatically pulls real traffic light locations from OpenStreetMap via the Overpass API.
- **Smart Intersections:** Automatically generates virtual traffic lights at major road junctions if real data is unavailable.
- **3-Stage Override System:**
  - **> 800m:** Signals operate normally (Red/Green/Yellow timers).
  - **< 800m:** Approaching signals turn **Yellow** to warn civilian traffic.
  - **< 500m:** Signals turn **Green** for immediate emergency clearance.
- **Traffic Density Simulation:** Adjustable settings for high/low density, affecting base signal timers and vehicle speed.

## Architecture

### Backend (Spring Boot & MySQL)
The backend exposes REST APIs to track the emergency vehicle location and manage the state of all traffic signals.

- **Controllers**: `TrafficController` handles routing and signal status. `UserController` handles authentication.
- **Services**: `TrafficService` contains the Haversine distance logic and the `@Scheduled` timer cycles.
- **Models**: `TrafficLight`, `Location`, `VehicleUser`, and `SignalStatus` (RED, GREEN, YELLOW).
- **Database**: Connected to a cloud MySQL instance (Aiven) for persistent user storage.

### Core API Endpoints

1.  **Update Ambulance Location**
    -   **Method**: `POST /api/ambulance/location`
    -   **Description**: Receives live GPS coordinates. Calculates distance to upcoming lights and triggers overrides.

2.  **Get Signal Status**
    -   **Method**: `GET /api/traffic-lights`
    -   **Description**: Returns an array of all active traffic lights, their colors, and their current countdown timers.

3.  **Batch Add Signals**
    -   **Method**: `POST /api/traffic-lights/batch`
    -   **Description**: Allows the frontend map to send newly detected real-world signals to the backend for management.

## Frontend (HTML/CSS/JS)
The frontend is embedded within the Spring Boot application (`src/main/resources/static`).
- **`login.html`**: Professional, responsive auth portal.
- **`index.html`**: Full-screen interactive map dashboard with live Google Maps traffic overlays and simulation controls.

## Running Locally

1.  Ensure MySQL is running locally and update `application.properties` with your root credentials.
2.  Run the `Main` class in `src/main/java/org/SmartEmergencyTrafficControl/Main.java`.
3.  The server will start on `http://localhost:8080`.
4.  Navigate to `http://localhost:8080/login.html` in your browser.