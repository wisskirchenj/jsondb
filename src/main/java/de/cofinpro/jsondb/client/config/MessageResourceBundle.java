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
    public static final String SENT_MSG_TEMPLATE = "Sent: %s";
}
