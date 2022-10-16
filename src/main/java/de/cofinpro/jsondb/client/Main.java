package de.cofinpro.jsondb.client;

import de.cofinpro.jsondb.client.controller.ClientController;
import de.cofinpro.jsondb.io.ConsolePrinter;

import java.io.IOException;

/**
 * Main class for the JsonDb Client
 */
public class Main {

    public static void main(String[] args) throws IOException {
        new ClientController(new ConsolePrinter()).send(args);
    }
}
