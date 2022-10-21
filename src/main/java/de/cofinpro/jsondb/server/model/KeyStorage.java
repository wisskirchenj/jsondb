package de.cofinpro.jsondb.server.model;

import de.cofinpro.jsondb.io.json.DatabaseResponse;

/**
 * implementing classes are themselves key storage databases or facades to one.
 */
public interface KeyStorage {
    
    DatabaseResponse set(String key, String value);

    DatabaseResponse get(String key);

    DatabaseResponse delete(String key);

    void close();
}
