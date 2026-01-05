For the ticket class and inheritance line, I have used a builder pattern with generics( for inheritance). 
Since the moment I saw optional fields, my mind flew to builder, also because I wanted to practice using it more.
For the users hierarchy, I've seen no reason to implement builder as there are too few fields to bother with it.

For the commands, I chose to implement the command pattern, with the help of an abstract class instead of interface. 
This decision came after seeing that every Command class had the same 2 fields. 
But the commands system is a bit more complicated.
There are exceptions associated with the restrictions that are validated before executing the commands.
The command abstract class contains GetRequiredRoles() and GetRequiredPhase() methods
that are to be overwritten by Command classes that have restrictions. Moreover, the commands
that can be executed by More than one Role are implemented with the Strategy Pattern. Each of these commands
has a strategy interface, which is implemented by the concrete strategies, and a context(for example viewTicketsCommand).
The context uses a factory to set the strategy and then implements the execute and/or undo method, and it can override the
permission functions if needed.

The class Repartition implements Comparable<> for ordering. The compareTo method sets
criteria for comparison.  
The class ViewTicketsCommand implements the sorting using an anonymous class inside the
execute() method.

For some strategies, like ViewMilestones, i used functional programming, because I wanted to get familiar to using streams(and why not, for bonus)

Because of the stupid checkstyle that says 'parameter' hides a field i added an s to all those parameters.



LLM's use: 
Would it have more sense for this method to be protected rather than private?
I used the LLM to configure a static block in order to corectly display the timestamp format
https://www.baeldung.com/java-builder-pattern-inheritance