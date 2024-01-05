package de.cofinpro.jsondb.client.controller;

import com.beust.jcommander.JCommander;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.io.json.DatabaseCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;
import static de.cofinpro.jsondb.io.json.GsonPooled.POOLED;

/**
 * controller class that is called by client's main via send() entry point.
 * .
 */
public class ClientController {

    private static final String CLIENT_DATA_PATH = "src/main/resources/client/data/";
    private final ConsolePrinter printer;

    public ClientController(ConsolePrinter printer) {
        this.printer = printer;
    }

    /**
     * send a request given by CL-args to the server via socket, receives an answer and terminates.
     * Server port and address are taken from a config class of the server package.
     * @throws IOException if a socket operation fails (creation, read, write)
     */
    public void send(String[] args) throws IOException {
        try (Socket client = new Socket(InetAddress.getByName(getSERVER_ADDRESS()), getSERVER_PORT())) {
            printer.printInfo(STARTED_MSG);
            String request = createRequestFromArgs(args);
            new DataOutputStream(client.getOutputStream()).writeUTF(request);
            printer.printInfo(SENT_MSG_TEMPLATE.formatted(request));
            printer.printInfo(RECEIVED_MSG_TEMPLATE.formatted(new DataInputStream(client.getInputStream()).readUTF()));
        }
    }

    /**
     * parses the CL arguments and maps it to a database command request, which is Jsonified.
     * If a filename is given, the command reqeust string is taken from the Json-file.
     * @param args the command line args to the client application
     * @return the Json-request string to send to the server via socket.
     */
    private String createRequestFromArgs(String[] args) throws IOException {
        DatabaseCommand command = new DatabaseCommand();
        JCommander.newBuilder()
                .addObject(command)
                .build()
                .parse(args);
        replaceListsByString(command);
        return command.getInputFilename() == null
                ? POOLED.gson().toJson(command)
                : Files.readString(Path.of(CLIENT_DATA_PATH + command.getInputFilename())).trim();
    }

    /**
     * Since key and value properties in the DatabaseCommand need to be Object to store Array and arbitrary Json,
     * Jcommander passes a single string as List<String>. This method checks and corrects that.
     */
    private void replaceListsByString(DatabaseCommand command) {
        if (command.getValue() instanceof List<?> list && list.get(0) instanceof String value) {
            command.setValue(value);
        }
        if (command.getKey() instanceof List<?> list && list.get(0) instanceof String key) {
            command.setKey(key);
        }
    }
}
