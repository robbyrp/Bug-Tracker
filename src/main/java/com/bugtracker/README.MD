# README Tema 2 POO
## Pana Robert-Ionut, grupa 323CA

The four main design patterns that I used are : builder, factory, command and strategy.

Now I will discuss the packages and their responsibilities
## Packages

```bash
command package
```
Implements the command pattern for the 20-something commands in the task. Some commands are implemented with Strategies.
```bash
enums package
```

Contains the enums necessary for the implementation of certain types of data. Some enums are very simple and contain no logic (I only use them semantically), while others contain logic. One such example is the ```Seniority``` enum, which contains the allowed Types and BusinessPriorities of tickets that a developer with said seniority can treat. For better error handling, some enums throw exceptions, which leads me to the next package.

```bash
exceptions package
```
Personalized exceptions that display error messages. It helps that I can directly insert their message in the json file when treating errors.

```bash
fileio package
```
It is very similar to the fileio package from Tema 1 Terrabot.
Contains the object parsing to and from JSON Files. Because certain commands require specific fields of classes to be outputted, I used multiple dto's for storing that data without altering the objects.
The ```outputFormatter``` helped me modularize code, as the outputting is made very easy thanks to the create_response_methods() which create the right type of output structure.

```bash
main package
```
Contains the two files that bind everything together. The App module instantiates the ```InputLoader```, the ```BugTrackerSystem```(the engine) as a singleton and the ```usersDatabase```, as there was no way to access that database JSON file from outside files.

As I said, ```BugTrackerSystem``` is the engine of the program. It validates the command inputs and calls the ```execute method``` (command pattern). Additionally, the databases and utility classes used in this module are all instantiated using Singleton, as there was no need for more than one instance of any of them.

```bash
milestone package
```
Contains the logic of the ```milestone class```, a database of milestones and an utility class that handles the automatic updates of milestones and assures that notifications are sent.

```bash
notification package
```
It contains the Subscriber interface, which is implemented by Users. The concrete update methods are called in the ```MilestoneManager``` module to send users relevant notifications. It is a simplified implementation of the ```Observer Pattern```. The class ```Repartition``` implements ```Comparable<>``` for ordering. The compareTo method sets criteria for comparison.

```bash
ticket package
```
Contains the ticket inheritance line, the ticketDatabase, the TicketAction class used for managing ticket history, and the TicketFactory, which implements both the Builder and Factory patterns. I will talk about this in more detail below.

```bash
user package
```
Similar to the ticket package, contains the user inheritance line, the database and the factory for creating users.

```bash
utils package
```
This package contains utilitary classes that handle the program flow and phases and the scores for the report command.


## Design Patterns
- Singleton is used to implement the databases and the engine class.

- Builder from the moment I saw optional fields, my mind went to the builder pattern. An interesting problem that I encountered was that the only way of not repeating the parent class's fields into the child's builder was to use a ```generic abstract builder inside the Ticket class``` to be extended by the children's builders. It was difficult to grasp at that moment, but in the end it proved to be very satisfying.
  Inspiration for this idea [here](https://www.baeldung.com/java-builder-pattern-inheritance).
  For the users hierarchy, I've seen no reason to implement builder as there are too few fields to bother with it.

- Command is implemented with an abstract class rather than an interface, so as not to give the same two parameters (user and input) to every execute method, separating concerns. There are exceptions associated with the restrictions that are validated before executing the commands.
  The command abstract class also contains GetRequiredRoles() and GetRequiredPhase() methods, that are to be overwritten by Command classes that have restrictions.
  oreover, I tried to experiment with the commands: some classes, such as ```ViewTicketsCommand``` implement ```sorting using an anonymous class``` inside the execute() method, while strategies such as such as ```ViewMilestones and AppStabilityReport```, used ```functional programming```, as I wanted to get familiar with using streams(and, why not, for bonus). The ```search``` command implements the search filters using functional programming aswell.

- Strategy is implemented in the ```command package```. The commands that can be executed by multiple types of users (e.g. Developer and Reporter) are implemented with the strategy pattern in their individual package, which contains the interface, the context and the concrete strategies. Multiple ```contexts contain factories``` for deciding which concrete strategy to use.

- Factory: I used it for the creation of Ticket and User objects, in the context of some strategies, to determine which concrete one to use. Another use case is returning the correct command class after reading the command name from the input.

- Observer: used for the notification logic.


## Workflow

1. The ```inputLoader class``` parses the Json input, the ```System``` is initialized as a Singleton, and the ```userDatabase``` is initialized from the ```userInputs class``` that maps the Database Json File. Then, ```system.execute(commands, outputs)``` is called, which moves the action to the ```System class```.
2. ```system.execute()``` method validates the username, the workflow phase, instantiates the correct Command Object using CommandFactory and calls ```command.execute(this, outputs)``` method. This method invokes the execute() method from the Concrete method's class. Before ```command.execute()```
   method is called, the system's ```Milestone Manager``` updates the milestones and the tickets within automatically.

## LLM Usage
I used LLM's mostly for :
- understanding the jackson library and json formatting
- Lombook, with its many adnotations and how it influences the json outputting

For example, it helped me figure out why the timestamp was displayed in the wrong format and proposed the static block in the Output Formatter to correctly display it.

## Thank you for reading this far)
