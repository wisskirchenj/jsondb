package de.cofinpro.jsondb.io.json;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing key storage database commands. Instances are filled by JCommander parsing of CL-arguments.
 */
@Getter
@NoArgsConstructor
public class DatabaseCommand {


    public DatabaseCommand(String type, Object key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    @Parameter(names = "-t", description = "command type")
    private String type;

    @Parameter(names = "-k", description = "storage key")
    @Setter
    private Object key;

    @Parameter(names = "-v", description = "Storage value")
    @Setter
    private Object value;

    @Parameter(names = "-in", description = "filename to read command from")
    private String inputFilename;
}
