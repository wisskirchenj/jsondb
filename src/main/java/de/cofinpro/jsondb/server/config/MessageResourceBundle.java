package de.cofinpro.jsondb.server.config;

/**
 * class to collect constants for server application messages.
 */
public class MessageResourceBundle {

    private MessageResourceBundle() {
        // no instances
    }

    public static final String STARTED_MSG = "Server started!";
    public static final String RECEIVED_MSG_TEMPLATE = "Received: %s";
    public static final String INVALID_REQUEST_MSG = "Invalid client request received!";
    public static final String ANSWER_TEMPLATE = "A %s was sent!";
    public static final String SENT_MSG_TEMPLATE = "Sent: %s";
}
