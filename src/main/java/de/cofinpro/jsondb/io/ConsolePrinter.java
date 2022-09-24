package de.cofinpro.jsondb.io;

import lombok.extern.slf4j.Slf4j;

/**
 * Log4j wrapper class to make stdout output easy mockable (Mockito.verify()) for unit testing.
 */
@Slf4j
public class ConsolePrinter {

    public void printInfo(String message) {
        log.info(message);
    }

    public void printError(String message) {
        log.error(message);
    }
}
