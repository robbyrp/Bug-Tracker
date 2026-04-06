//package com.bugtracker;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.ObjectWriter;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.bugtracker.fileio.CommandInput;
//import com.bugtracker.fileio.InputLoader;
//import com.bugtracker.fileio.UserInput;
//import lombok.NoArgsConstructor;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * main.App represents the main application logic that processes input commands,
// * generates outputs, and writes them to a file
// */
//@NoArgsConstructor
//public final class App {
//
//    private static final String INPUT_USERS_FIELD = "input/database/users.json";
//
//    private static final ObjectWriter WRITER =
//            new ObjectMapper().writer().withDefaultPrettyPrinter();
//
//    /**
//     * Runs the application: reads commands from an input file,
//     * processes them, generates results, and writes them to an output file
//     *
//     * @param inputPath path to the input file containing commands
//     * @param outputPath path to the file where results should be written
//     */
//    public static void run(final String inputPath, final String outputPath) {
//        // however keep 'outputs' variable name to be used for writing
//        ObjectMapper mapper = new ObjectMapper();
//        List<ObjectNode> outputs = new ArrayList<>();
//
//        try {
//            ArrayList<UserInput> userInputs = mapper.readValue(
//                    new File(INPUT_USERS_FIELD),
//                    new TypeReference<ArrayList<UserInput>>() { }
//            );
//
//            InputLoader inputLoader = new InputLoader(inputPath);
//            List<CommandInput> commands = inputLoader.getCommands();
//
//            BugTrackerSystem system = new BugTrackerSystem();
//
//            system.getUserDatabase().initialize(userInputs);
//
//            system.executeCommands(commands, outputs);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // DO NOT CHANGE THIS SECTION IN ANY WAY
//        try {
//            File outputFile = new File(outputPath);
//            outputFile.getParentFile().mkdirs();
//            WRITER.withDefaultPrettyPrinter().writeValue(outputFile, outputs);
//        } catch (IOException e) {
//            System.out.println("error writing to output file: " + e.getMessage());
//        }
//    }
//}
