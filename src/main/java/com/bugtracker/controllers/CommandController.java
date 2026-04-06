package com.bugtracker.controllers;

import com.bugtracker.BugTrackerSystem;
import com.bugtracker.fileio.CommandInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/commands")
public class CommandController {

    private final BugTrackerSystem bugTrackerSystem;
    public CommandController(final BugTrackerSystem bugTrackerSystem) {
        this.bugTrackerSystem = bugTrackerSystem;
    }

    @PostMapping("/batch")
    public List<ObjectNode> executeBatchCommands(@RequestBody List<CommandInput> commands) {
        List<ObjectNode> outputs = new ArrayList<>();
        bugTrackerSystem.executeCommands(commands, outputs);
        return outputs;
    }

    @PostMapping
    public ObjectNode executeCommand(@RequestBody CommandInput commandInput) {
        return bugTrackerSystem.executeSingleCommand(commandInput);
    }

    @GetMapping("/reset")
    public ObjectNode resetSystem() {
        bugTrackerSystem.getTicketDatabase().getTickets().clear();
        bugTrackerSystem.getMilestoneDatabase().getMilestoneList().clear();
        bugTrackerSystem.getDateManager().reset();
        bugTrackerSystem.setActiveStatus(true);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("status", "SUCCESS");
        response.put("message", "Memory has been reset. System is ready for a new batch!");

        return response;
    }

    /**
     * Endpoint for handling complex input files
     * @param file
     * @return
     */
    @PostMapping(value = "/uploadBatch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ObjectNode> uploadBatchFile(@RequestParam("file") MultipartFile file) {
        List<ObjectNode> outputs = new ArrayList<>();

        if (file.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "File loaded is empty or missing!");
            outputs.add(errorNode);
            return outputs;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<CommandInput> commands = mapper.readValue(
                    file.getInputStream(),
                    new TypeReference<List<CommandInput>>() {}
            );

            bugTrackerSystem.executeCommands(commands, outputs);

        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("error", "Error reading JSON file: " + e.getMessage());
            outputs.add(errorNode);
        }

        return outputs;
    }


}
