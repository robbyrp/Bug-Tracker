# Bug Tracker REST API

A backend application built with **Spring Boot 3.4** for managing developer workflows, bug reporting, and developer assignments. 
This project implements a stateful system using Object-Oriented Programming principles and structural design patterns.

### Prerequisites
* Java 17 or higher
* Maven

### Starting the Application
1. Clone the repository and navigate to the project directory.
2. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```
3. The server will start on port `8080`.
### API Documentation & Testing (Swagger UI)
This project includes a built-in Swagger UI, making it extremely easy to test without Postman.
Once the application is running, open your browser and navigate to:
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

## How to test using the provided examples:
1. Expand the `POST /batch` endpoint.
2. Click **Try it out**.
3. Click **Choose File** and select one of the sample `.json` files provided in the `input/` directory of this repository.
4. Click **Execute** to see the system's output.
5. **Important:** Use the `GET /reset` endpoint to clear the memory and reset the system's state before uploading a new test file.

## Workflow

1. A JSON file containing a batch of commands is uploaded via the `/batch` endpoint in the `CommandController`.
2. The `BugTrackerSystem` (acting as the engine) is injected. The `userDatabase` is initialized at startup.
3. The system validates the current workflow phase and instantiates the correct Command Object using `CommandFactory`.
4. Before a command is executed, the system's `MilestoneManager` updates the milestones and the tickets within automatically.
5. `command.execute()` invokes the specific logic, modifying the system state and appending the formatted result to the JSON output list returned to the client.
6. The controller logic in the endpoints is very simple, as all the heavy work is done by the Command Pattern.

# OOP

- **Singleton:** Used to implement the databases and the engine class (`BugTrackerSystem`)
- **Builder:** Heavily utilized for entities with optional fields. An interesting challenge was inheriting builder fields; the solution involved using a **generic abstract builder** inside the `Ticket` parent class, which was then extended by the children's builders.
- **Command:** The command abstract class handles pre-execution validations (like `getRequiredRoles()` and `getRequiredPhase()`).
- **Strategy:** Commands that can be executed differently by multiple types of users (e.g., Developer vs. Reporter) use the strategy pattern. Multiple contexts contain factories to decide which concrete strategy to use at runtime.
- **Factory:** Used for the creation of `Ticket` and `User` objects, and for returning the correct `Command` class after reading the command name from the API input.
- **Observer:** A simplified implementation is used in the notification logic (the `Subscriber` interface implemented by Users, updated by the `MilestoneManager`).

## Structure
* **Custom Exception Handling** 
* **Smart Enums** 
* **Data Transfer Objects (DTOs)**
* **Dynamic JSON Processing**
* **Functional Programming** 