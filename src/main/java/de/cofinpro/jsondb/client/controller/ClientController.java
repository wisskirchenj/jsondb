package de.cofinpro.jsondb.client.controller;

import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.server.model.CellDatabase;
import de.cofinpro.jsondb.client.config.MessageResourceBundle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;

/**
 * controller class that is called by main via run() entry point. It does the command loop and calls the cell database.
 */
public class ClientController {

    private static final Random RANDOM = new Random();

    private final CellDatabase jsonDb = new CellDatabase();
    private final ConsolePrinter printer;

    public ClientController(ConsolePrinter printer) {
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
            printer.printInfo(MessageResourceBundle.ERROR_MSG);
            return;  // all commands other then exit have at least an int parameter
        }
        int index = parseIndex(tokens[1]);
        printer.printInfo(switch (tokens[0]) {
            case "set" -> jsonDb.set(index, tokens.length < 3 ? "" : tokens[2]);
            case "get" -> jsonDb.get(index);
            case "delete" -> jsonDb.delete(index);
            default -> MessageResourceBundle.ERROR_MSG;
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

    /**
     * send a request for a random record (# 0 to 99) to the server socket. Server port and address are taken
     * from a config class of the server package.
     * @throws IOException if a socket operation fails (creation, read, write)
     */
    public void send() throws IOException {
        try (Socket client = new Socket(InetAddress.getByName(getSERVER_ADDRESS()), getSERVER_PORT())) {
            printer.printInfo(STARTED_MSG);
            String request = REQUEST_TEMPLATE.formatted(RANDOM.nextInt(100));
            new DataOutputStream(client.getOutputStream()).writeUTF(request);
            printer.printInfo(SENT_MSG_TEMPLATE.formatted(request));
            printer.printInfo(RECEIVED_MSG_TEMPLATE.formatted(new DataInputStream(client.getInputStream()).readUTF()));
        }
    }
}
