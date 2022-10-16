package de.cofinpro.jsondb.server.controller;

import com.beust.jcommander.Parameter;
import lombok.Getter;

import java.util.List;

@Getter
public class DatabaseCommand {

    @Parameter(names = "-t", description = "command type")
    private String command;

    @Parameter(names = "-i", description = "cell index")
    private Integer cellIndex;

    @Parameter(names = "-m", description = "Message for set command", variableArity = true)
    private List<String> message;

    public String getMessage() {
        return String.join(" ", message);
    }
}
