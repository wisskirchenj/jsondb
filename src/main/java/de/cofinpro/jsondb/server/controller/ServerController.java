package de.cofinpro.jsondb.server.controller;

import com.beust.jcommander.JCommander;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.server.model.CellDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static de.cofinpro.jsondb.server.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;

/**
 * controller class for the JsonDb-Server
 */
public class ServerController {

    private final ConsolePrinter printer;
    private final CellDatabase database = new CellDatabase();
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
        boolean exitRequested = false;
        while (!exitRequested) {
            exitRequested = acceptOneClient();
        }
        server.close();
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
     * @return true, if client requests exit, false else
     * @throws IOException if some socket operation fails
     */
    private boolean acceptOneClient() throws IOException {
        Socket client = server.accept();
        String clientRequest = new DataInputStream(client.getInputStream()).readUTF();
        printer.printInfo(RECEIVED_MSG_TEMPLATE.formatted(clientRequest));
        DatabaseCommand command = parseRequest(clientRequest);
        answerClientRequest(client, command);
        client.close();
        return command.getCommand().equals("exit");
    }

    private DatabaseCommand parseRequest(String clientRequest) {
        DatabaseCommand command = new DatabaseCommand();
        JCommander.newBuilder()
                .addObject(command)
                .build()
                .parse(clientRequest.split(" "));
        return command;
    }

    /**
     * answers a client request. The database command is executed if recognized - if not an
     * invalid message is sent.
     * @param client the socket to the connected client
     * @param command the parsed database command
     * @throws IOException if some socket operation fails
     */
    private void answerClientRequest(Socket client, DatabaseCommand command) throws IOException {
        String answer = switch (command.getCommand()) {
            case "set" -> database.set(command.getCellIndex(), command.getMessage());
            case "get" -> database.get(command.getCellIndex());
            case "delete" -> database.delete(command.getCellIndex());
            case "exit" -> OK_MSG;
            default -> INVALID_REQUEST_MSG;
        };
        new DataOutputStream(client.getOutputStream()).writeUTF(answer);
        printer.printInfo(SENT_MSG_TEMPLATE.formatted(answer));
    }
}
