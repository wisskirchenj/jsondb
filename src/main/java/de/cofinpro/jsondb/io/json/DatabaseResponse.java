package de.cofinpro.jsondb.io.json;

import com.google.gson.annotations.SerializedName;

import static de.cofinpro.jsondb.server.config.MessageResourceBundle.*;

/**
 * immutable record that represents a database response.
 */
public record DatabaseResponse(String response, @SerializedName("value") String payload,
                               @SerializedName("reason") String errorReason) {

    private static final DatabaseResponse OK = new DatabaseResponse(OK_MSG, null, null);
    private static final DatabaseResponse ERROR
            = new DatabaseResponse(ERROR_MSG, null, NO_SUCH_KEY_MSG);

    public static DatabaseResponse ok() {
        return OK;
    }

    public static DatabaseResponse error() {
        return ERROR;
    }
}
