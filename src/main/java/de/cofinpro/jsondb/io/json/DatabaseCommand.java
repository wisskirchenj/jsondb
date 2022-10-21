package de.cofinpro.jsondb.io.json;

import com.beust.jcommander.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class representing key storage database commands. Instances are filled by JCommander parsing of CL-arguments.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseCommand {

    @Parameter(names = "-t", description = "command type")
    private String type;

    @Parameter(names = "-k", description = "storage key")
    private String key;

    @Parameter(names = "-v", description = "Storage value")
    private String value;
}
