For the ticket class and inheritance line, I have used a builder pattern with generics( for inheritance). 
Since the moment I saw optional fields, my mind flew to builder, also because I wanted to practice using it more.
For the users hierarchy, I've seen no reason to implement builder as there are too few fields to bother with it.
For the commands, I chose to implement the command pattern, with the help of an abstract class instead of interface. 
This decision came after seeing that every Command class had the same 2 fields. 

Because of the stupid checkstyle that says 'parameter' hides a field i added an s to all those parameters.



LLM's use: 
Would it have more sense for this method to be protected rather than private?
I used the LLM to configure a static block in order to corectly display the timestamp format
https://www.baeldung.com/java-builder-pattern-inheritance