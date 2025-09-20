# Electronic Circuit Simulator

**25SP CSC311 – Advanced Programming (25611)**

An interactive drag-and-drop tool for designing and simulating electrical circuits.

---

## Table of Contents

- [Brief Description](#brief-description)  
- [Features](#features)  
- [Getting Started](#getting-started)  
  - [Prerequisites](#prerequisites)  
  - [Installation & Configuration](#installation--configuration)  
- [Tech Stack](#tech-stack)  
- [Project Architecture](#project-architecture)  
- [How It Works](#how-it-works)  
- [Usage](#usage)  
- [Status](#status)  
- [Credits](#credits)  

---

## Brief Description

This project’s main purpose is to let users register, then drag-and-drop electrical components (batteries, resistors, switches, wires, bulbs) onto a large, zoomable canvas and simulate circuit behavior in real time :contentReference[oaicite:0]{index=0}.

---

## Features

- **Multi-Project Management**  
  Create, save, load, rename, and delete multiple circuit projects.  
- **Drag & Drop Components**  
  Place and move batteries, resistors, switches, wires, and light bulbs.  
- **Real-Time Feedback**  
  Display voltage, current, and resistance feedback as components are added or altered.  
- **Zoomable Canvas**  
  Pan and zoom for precise component placement.  
- **Persistent Storage**  
  Projects and layouts are stored in an Azure-backed MySQL database.  

---

## Getting Started

### Prerequisites

- **Java 23+ JDK**  
- **Maven**  
- **MySQL** (local or Azure-hosted)  
- **IntelliJ IDEA** (or another Java IDE)  

### Installation & Configuration

1. **Clone the repo**  
   ```bash
   git clone https://github.com/stevenlin0/CircuitSimulator.git
   cd CircuitSimulator
2. Configure database
  Copy src/main/resources/db.properties.example → src/main/resources/db.properties.
  Edit with your MySQL credentials and JDBC URL, e.g.:
    db.url=jdbc:mysql://<YOUR_HOST>:3306/circuit_sim
    db.username=your_username
    db.password=your_password

## Tech Stack

- **Frontend:** JavaFX (FXML + CSS), Scene Builder  
- **Backend:** Java 17, Maven, JDBC MySQL Connector  
- **Database:** Azure MySQL  
- **Tools & Design:** Figma, GitHub, IntelliJ IDEA  

## Project Architecture

- **ConnDbOps**  
  JDBC utility for CRUD on users, projects, and components.

- **Controllers**  
  - `SignInController` / `SignUpController` – Authentication flows  
  - `ProjectController` – Main canvas, toolbar actions, simulation triggers  
  - `SettingsController` – User account settings  

- **Models & Nodes**  
  - **Models:** `BatteryModel`, `ResistorModel`, `LightbulbModel`, `TerminalModel`, etc.  
  - **Nodes:** `BatteryNode`, `ResistorNode`, `WireNode`, `TerminalNode`  

- **Simulation Engine**  
  Methods in `ProjectController` (e.g., `simulateCircuit()`) handle power propagation and update bulb visuals.

## Usage

- **Add Component:** Click a toolbar icon, then click the canvas  
- **Move Component/Wire:** Click and drag  
- **Zoom/Pan:** Scroll or use trackpad gestures  
- **Save Project:** Use the **save**
- **Load/Delete Project:** Select from the project list at launch
