package de.cofinpro.jsondb.server.controller;

import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.io.json.DatabaseCommand;
import de.cofinpro.jsondb.io.json.DatabaseResponse;
import de.cofinpro.jsondb.io.json.GsonPooled;
import de.cofinpro.jsondb.server.model.FileKeyStorage;
import de.cofinpro.jsondb.server.model.KeyStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static de.cofinpro.jsondb.server.config.MessageResourceBundle.*;
import static de.cofinpro.jsondb.io.SocketConfig.*;

/**
 * controller class for the JsonDb-Server using a Jedis-based kex storage to Redis.
 */
public class ServerController {

    private final ConsolePrinter printer;
    private final KeyStorage database = new FileKeyStorage();
    private ServerSocket server;

    public ServerController(ConsolePrinter printer) {
        this.printer = printer;
    }

    /**
     * single entry point called from main.
     * A server(socket) is created on the configured port which listens for incoming single client requests
     * and handles them - until a client sends an exit command is incoming. Then the server stops.
     * @throws IOException in case server or client socket creation or read/write method fails
     */
    public void run() throws IOException {
        startServer();
        acceptClientsMultiThreaded();
        server.close();
        database.close();
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
     * use an ExecutorService to handle clients asynchronously, until one client requests the server to exit, in which
     * case the ExecutorService is shutdown.
     */
    private void acceptClientsMultiThreaded() throws IOException {
        boolean exitRequested = false;
        var executor = Executors.newFixedThreadPool(10);
        try {
            while (!exitRequested) {
                exitRequested = acceptOneClient(executor);
            }
        } finally {
            shutDownExecutorAndWait(executor);
        }
    }

    /**
     * needed for tests to wait for all threads to finish - normal program runs can live without awaitTermination...
     * @param executor the executor service to shut down and wait for
     */
    private void shutDownExecutorAndWait(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) { // to finish started db-access threads
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * listens for one client request and handles it. While accept and "command unwrap" is done synchronously,
     * the answer - which requires DB-access - is run in a new thread from the executor pool. Thus exit is returned
     * synchronously to the accept loop, i.e. the server stops (already submitted DB-request still finish).
     * @return true, if client requests exit, false else
     * @throws IOException if some socket operation fails
     */
    private boolean acceptOneClient(ExecutorService executor) throws IOException {
        Socket client = server.accept();
        String clientRequest = new DataInputStream(client.getInputStream()).readUTF();
        printer.printInfo(RECEIVED_MSG_TEMPLATE.formatted(clientRequest));
        DatabaseCommand command = GsonPooled.getGson().fromJson(clientRequest, DatabaseCommand.class);
        executor.submit(() -> {
            try {
                answerClientRequest(client, command);
                client.close();
            } catch (Exception e) {
                printer.printError(e.toString());
            }
        });
        return command.getType().equals("exit");
    }

    /**
     * answers a client request. The database command is executed if recognized and the response mapped to
     * a DatabaseResponse, that is Jsonified and sent.
     * @param client the socket to the connected client
     * @param command the received database command
     * @throws IOException if some socket operation fails
     */
    private void answerClientRequest(Socket client, DatabaseCommand command) throws IOException {
        DatabaseResponse answer = switch (command.getType()) {
            case "set" -> database.set(command.getKey(), command.getValue());
            case "get" -> database.get(command.getKey());
            case "delete" -> database.delete(command.getKey());
            case "exit" -> DatabaseResponse.ok();
            default -> new DatabaseResponse(ERROR_MSG, null, INVALID_REQUEST_MSG);
        };
        new DataOutputStream(client.getOutputStream()).writeUTF(GsonPooled.getGson().toJson(answer));
        printer.printInfo(SENT_MSG_TEMPLATE.formatted(answer));
    }
}
