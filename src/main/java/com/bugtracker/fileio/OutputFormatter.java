package com.bugtracker.fileio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public final class OutputFormatter {
    private static ObjectMapper mapper = new ObjectMapper();

    private OutputFormatter() { }
    /**
     *
     */
    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Helper method for common header
     * @param command
     * @param username
     * @param timestamp
     * @return
     */
    private static ObjectNode createBaseNode(final String command,
                                             final String username, final String timestamp) {
        ObjectNode node = mapper.createObjectNode();
        node.put("com/bugtracker/command", command);
        node.put("username", username);
        node.put("timestamp", timestamp);
        return node;
    }

    /**
     * Method for error message
     * @param command
     * @param username
     * @param timestamp
     * @param errorMessage
     * @return
     */
    public static ObjectNode createError(final String command,
                                         final String username, final String timestamp,
                                         final String errorMessage) {
        ObjectNode node = createBaseNode(command, username, timestamp);
        node.put("error", errorMessage);
        return node;
    }

    /**
     * Standard list  for most commands' outputs
     * @param command
     * @param username
     * @param timestamp
     * @param listName
     * @param items
     * @return
     */
    public static ObjectNode createListResponse(final String command,
                                         final String username, final String timestamp,
                                         final String listName, final List<?> items) {
        ObjectNode node = createBaseNode(command, username, timestamp);

        node.set(listName, mapper.valueToTree(items));

        return node;
    }

    /**
     * Method for report output
     * @param command
     * @param username
     * @param timestamp
     * @param keyName
     * @param reportObject
     * @return
     */
    public static ObjectNode createReportResponse(final String command, final String username,
                                                  final String timestamp, final String keyName,
                                                  final Object reportObject) {
        ObjectNode node = createBaseNode(command, username, timestamp);

        node.set(keyName, mapper.valueToTree(reportObject));

        return node;
    }

    /**
     * Method for search output
     * @param command
     * @param username
     * @param timestamp
     * @param searchType
     * @param results
     * @return
     */
    public static ObjectNode createSearchResponse(final String command,
                                                  final String username,
                                                  final String timestamp,
                                                  final String searchType,
                                                  final List<?> results) {
        ObjectNode node = createBaseNode(command, username, timestamp);

        if (searchType != null) {
            node.put("searchType", searchType);
        }

        node.set("results", mapper.valueToTree(results));

        return node;
    }

    /**
     * Converts generic object to jsonNode
     * @param object
     * @return
     */
    public static JsonNode mapToNode(final Object object) {
        return mapper.valueToTree(object);
    }

}
