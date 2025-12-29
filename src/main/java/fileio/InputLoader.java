package fileio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                new TypeReference<ArrayList<CommandInput>>(){}
        );
    }
}
