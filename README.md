# Air Traffic Simulation 

## Overview

This project is a **simulation of air traffic** within a rectangular map, designed using Object-Oriented Software Engineering (OOSE) principles and design patterns. The system models various elements like airports, planes, and their interactions during flight and servicing. The simulation uses **multi-threading**, **blocking queues**, and **thread pools** to manage the dynamic aspects of the simulation, ensuring smooth and efficient operation under different conditions.

### Key Features:
- **Airport and Plane Management**: The simulation sets up **N_A (number of airports)** randomly placed across the map, each with a unique ID. Each airport has **N_P (number of planes)** planes that can change locations based on flight requests.
- **Plane Flight Simulation**: Planes will fly between airports according to flight requests at a constant speed `S` (1 unit per second).
- **Service Time**: After each plane lands at its destination airport, it undergoes a service for a varying period of time determined by another external system.
- **Design Patterns Used**:
  - **Factory Pattern**: For creating instances of airports and planes.
  - **Blocking Queue**: For coordinating the planes and processing flight requests between different threads.
  - **Thread Pool**: For managing tasks related to planes' flight simulation, such as updating plane positions and showing airport activities on the GUI.

### Multi-threading and Parallelism:
- **Blocking Queue**: We use a blocking queue to manage the transfer of flight requests between different threads. When a plane lands at a new airport, a service request is placed in the queue, which another thread will then process to simulate the service period.
- **Thread Pool**: Tasks like updating plane positions and displaying airport activities are offloaded to a thread pool for efficient handling. This prevents the main thread from getting blocked during simulations and ensures the system remains responsive.

### Thread Safety
- All threads interact with shared resources (like airports, planes, and service queues) in a coordinated and synchronized manner using proper locking mechanisms to prevent race conditions or deadlocks. We use **synchronized blocks** and **atomic variables** to safely manage access to shared resources.
