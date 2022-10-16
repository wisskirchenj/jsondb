package de.cofinpro.jsondb.client.controller;

import de.cofinpro.jsondb.io.ConsolePrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;

/**
 * controller class that is called by main via run() entry point. It does the command loop and calls the cell database.
 */
public class ClientController {

    private final ConsolePrinter printer;

    public ClientController(ConsolePrinter printer) {
        this.printer = printer;
    }

    /**
     * send a request for a random record (# 0 to 99) to the server socket. Server port and address are taken
     * from a config class of the server package.
     * @throws IOException if a socket operation fails (creation, read, write)
     */
    public void send(String[] args) throws IOException {
        try (Socket client = new Socket(InetAddress.getByName(getSERVER_ADDRESS()), getSERVER_PORT())) {
            printer.printInfo(STARTED_MSG);
            String request = String.join(" ", args);
            new DataOutputStream(client.getOutputStream()).writeUTF(request);
            printer.printInfo(SENT_MSG_TEMPLATE.formatted(request));
            printer.printInfo(RECEIVED_MSG_TEMPLATE.formatted(new DataInputStream(client.getInputStream()).readUTF()));
        }
    }
}
