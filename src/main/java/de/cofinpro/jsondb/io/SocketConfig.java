package de.cofinpro.jsondb.io;

import lombok.Getter;

/**
 * helper class to specify the server configuration parameters
 */
public class SocketConfig {

    private SocketConfig() {
        // no instances
    }

    @Getter
    private static final String SERVER_ADDRESS = "127.0.0.1";

    @Getter
    private static final int SERVER_PORT = 23456;

    @Getter
    private static final int SERVER_BACKLOG_QUEUE = 50;
}
