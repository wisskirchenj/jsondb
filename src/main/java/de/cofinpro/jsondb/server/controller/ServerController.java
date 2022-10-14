package de.cofinpro.jsondb.server.controller;

import de.cofinpro.jsondb.io.ConsolePrinter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.cofinpro.jsondb.server.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;

/**
 * controller class for the JsonDb-Server
 */
public class ServerController {

    private final ConsolePrinter printer;
    private ServerSocket server;

    public ServerController(ConsolePrinter printer) {
        this.printer = printer;
    }

    /**
     * single entry point called from main.
     * A server(socket) is created on the configured port which listens until one single client request is incoming.
     * The request is handled and the server stops.
     * @throws IOException in case server or client socket creation or read/write method fails
     */
    public void run() throws IOException {
        startServer();
        acceptOneClient();
    }

    /**
     * create a ServerSocket with configuration data taken from a config class
     * @throws IOException if socket creation fails
     */
    private void startServer() throws IOException {
        server = new ServerSocket(getSERVER_PORT(), getSERVER_BACKLOG_QUEUE(),
                InetAddress.getByName(getSERVER_ADDRESS()));
        printer.printInfo(STARTED_MSG);
    }

    /**
     * listen for one client request and handle it
     * @throws IOException if some socket operation fails
     */
    private void acceptOneClient() throws IOException {
        Socket client = server.accept();
        String clientRequest = new DataInputStream(client.getInputStream()).readUTF();
        printer.printInfo(RECEIVED_MSG_TEMPLATE.formatted(clientRequest));
        answerClientRequest(client, clientRequest);
    }

    /**
     * answers a client request. If the request format is valid, the requested record is delivered - if not an
     * invalid message is sent.
     * @param client the socket to the connected client
     * @param clientRequest the client request to answer
     * @throws IOException if some socket operation fails
     */
    private void answerClientRequest(Socket client, String clientRequest) throws IOException {
        Matcher recordMatcher = Pattern.compile("record # \\d+").matcher(clientRequest);
        String answer = recordMatcher.find()
                ? ANSWER_TEMPLATE.formatted(recordMatcher.group())
                : INVALID_REQUEST_MSG;
        new DataOutputStream(client.getOutputStream()).writeUTF(answer);
        printer.printInfo(SENT_MSG_TEMPLATE.formatted(answer));
    }
}
