package de.cofinpro.jsondb.io.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * singleton wrapper (eagerly initialized since definitely needed right away) to an application-wide
 * shared Gson object (which is thread-safe).
 */
public class GsonPooled {

    private static final Gson GSON = new GsonBuilder().create();

    private GsonPooled() {
        // no instances - implements singleton access to Gson object
    }

    public static Gson getGson() {
        return GSON;
    }
}
