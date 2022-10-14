package de.cofinpro.jsondb.client.config;

/**
 * class to collect constants for client application messages.
 */
public class MessageResourceBundle {

    private MessageResourceBundle() {
        // no instances
    }

    public static final String STARTED_MSG = "Client started!";
    public static final String RECEIVED_MSG_TEMPLATE = "Received: %s";
    public static final String REQUEST_TEMPLATE = "Give me a record # %d";
    public static final String SENT_MSG_TEMPLATE = "Sent: %s";
    public static final String ERROR_MSG = "ERROR";
    public static final String OK_MSG = "OK";
}
