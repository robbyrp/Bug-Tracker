# Bug Tracker & Management Engine

## Overview
A comprehensive, backend-focused Java application designed to simulate a real-world issue tracking system. It operates as a robust, centralized state machine that processes sequential commands to manage user actions, ticket lifecycles, and project milestones, while strictly enforcing business rules and generating detailed system analytics.

## How It Works
The application functions as a batch-processing engine that handles complex data flows without requiring a traditional user interface:

* **Data Ingestion:** The system parses complex JSON input files representing a chronological sequence of events (e.g., reporting bugs, requesting features, assigning tasks, changing ticket statuses).
* **Command Dispatch & Validation:** Each parsed event is dynamically mapped to a specific action. Before execution, a centralized engine validates user roles, hierarchy permissions, and state constraints.
* **State Management:** The core engine maintains the exact lifecycle of all entities. It automatically handles priority escalations based on time constraints and manages complex milestone dependencies (e.g., automatically unblocking dependent milestones when blocker tickets are resolved).
* **Output & Analytics:** Upon processing the event queue, the system serializes the final state into structured JSON outputs. This includes action histories, error logs, and comprehensive system metrics such as Application Stability, Customer Impact, and Developer Performance reports.

## Architecture & Design Patterns
The project is heavily rooted in Object-Oriented Programming (OOP) principles and leverages multiple structural and behavioral design patterns to ensure scalability and clean code architecture:

* **Command Pattern:** Forms the backbone of the execution engine, decoupling the invoker from the actual business logic of the 20+ supported system actions.
* **Strategy & Factory Patterns:** Used for dynamic object instantiation and routing complex business logic based on user roles and ticket types.
* **Generic Builder Pattern:** Implemented to handle the complex, hierarchical creation of objects with numerous optional fields.
* **Observer Pattern:** Drives the internal notification system, alerting specific users when milestone states change or deadlines approach.
* **Singleton Pattern:** Ensures centralized, conflict-free management of the simulated databases and the core execution engine.

## Tech Stack
* **Language:** Java
* **Build Automation:** Maven
* **Data Processing:** Jackson (JSON Serialization/Deserialization)
* **Testing & Verification:** JUnit, automated JSON output validation
