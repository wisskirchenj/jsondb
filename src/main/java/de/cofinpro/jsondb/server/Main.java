package de.cofinpro.jsondb.server;

import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.server.controller.ServerController;

import java.io.IOException;

/**
 * Main class for the JsonDb Server
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new ServerController(new ConsolePrinter()).run();
    }
}