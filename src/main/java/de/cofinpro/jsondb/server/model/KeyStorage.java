package de.cofinpro.jsondb.server.model;

import de.cofinpro.jsondb.io.json.DatabaseResponse;

/**
 * implementing classes are themselves key storage databases or facades to one.
 */
public interface KeyStorage {
    
    DatabaseResponse set(Object key, Object value);

    DatabaseResponse get(Object key);

    DatabaseResponse delete(Object key);

    void close();
}
