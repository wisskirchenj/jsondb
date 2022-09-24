package de.cofinpro.jsondb.controller;

import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.model.CellDatabase;

import java.util.Scanner;

import static de.cofinpro.jsondb.config.MessageResourceBundle.ERROR_MSG;
import static de.cofinpro.jsondb.config.MessageResourceBundle.UNKNOWN_COMMAND_MSG;

/**
 * controller class that is called by main via run() entry point. It does the command loop and calls the cell database.
 */
public class JsonDbController {

    private final CellDatabase jsonDb = new CellDatabase();
    private final ConsolePrinter printer;

    public JsonDbController(ConsolePrinter printer) {
        this.printer = printer;
    }

    /**
     * entry point for Main that implements the CL-loop
     * @param scanner a scanner is given as parameter to be able to mock it for tests.
     */
    public void run(Scanner scanner) {
        String command = scanner.nextLine();
        while (!"exit".equals(command)) {
            processCommand(command);
            command = scanner.nextLine();
        }
    }

    /**
     * parse the command line given, call the CellDatabase if command recognized and print the result info.
     * @param command a command line different from "exit"
     */
    private void processCommand(String command) {
        String[] tokens = command.split("\\s+", 3);
        if (tokens.length < 2) {
            printer.printInfo(ERROR_MSG);
            return;  // all commands other then exit have at least an int parameter
        }
        int index = parseIndex(tokens[1]);
        printer.printInfo(switch (tokens[0]) {
            case "set" -> jsonDb.set(index, tokens.length < 3 ? "" : tokens[2]);
            case "get" -> jsonDb.get(index);
            case "delete" -> jsonDb.delete(index);
            default -> UNKNOWN_COMMAND_MSG;
        });
    }

    /**
     * parse the index given for all valid commands
     * @param indexToken token to parse for an index
     * @return -1 if indexToken cannot be parsed, the parse result else
     */
    private int parseIndex(String indexToken) {
        try {
            return Integer.parseInt(indexToken);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
