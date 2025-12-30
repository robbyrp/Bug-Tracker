package fileio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Basic skeleton for loading input JSON file as a Map.
 * Students should implement deeper parsing themselves.
 */
@Getter
public final class InputLoader {
    private final ArrayList<CommandInput> commands;

    public InputLoader(final String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        this.commands = mapper.readValue(
                new File(filePath),
                new TypeReference<ArrayList<CommandInput>>() { }
        );
    }
}
