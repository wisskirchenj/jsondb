package de.cofinpro.jsondb;

import de.cofinpro.jsondb.controller.JsonDbController;
import de.cofinpro.jsondb.io.ConsolePrinter;

import java.util.Scanner;

/**
 * Main class for the JsonDb Server
 */
public class Main {
    public static void main(String[] args) {
        new JsonDbController(new ConsolePrinter()).run(new Scanner(System.in));
    }
}