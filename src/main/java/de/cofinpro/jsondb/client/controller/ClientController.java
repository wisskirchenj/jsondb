package de.cofinpro.jsondb.client.controller;

import com.beust.jcommander.JCommander;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.io.json.DatabaseCommand;
import de.cofinpro.jsondb.io.json.GsonPooled;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;

/**
 * controller class that is called by client's main via send() entry point.
 * .
 */
public class ClientController {

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
     * parses the CL arguments and maps it to a database command request
     * @param args the command line args to the client application
     * @return the Json-request string to send to the server via socket.
     */
    private String createRequestFromArgs(String[] args) {
        DatabaseCommand command = new DatabaseCommand();
        JCommander.newBuilder()
                .addObject(command)
                .build()
                .parse(args);
        return GsonPooled.getGson().toJson(command);
    }
}
