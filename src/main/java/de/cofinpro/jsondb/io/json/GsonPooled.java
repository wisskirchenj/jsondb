package de.cofinpro.jsondb.io.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Enum singleton wrapper to an application-wide
 * shared Gson object (which is thread-safe).
 */
public enum GsonPooled {

    POOLED;

    private final Gson gson;

    GsonPooled() {
        this.gson = new GsonBuilder().create();
    }

    public Gson gson() {
        return this.gson;
    }
}
