package de.cofinpro.jsondb.server.model;

import com.google.gson.reflect.TypeToken;
import de.cofinpro.jsondb.io.json.DatabaseResponse;
import de.cofinpro.jsondb.io.json.GsonPooled;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static de.cofinpro.jsondb.server.config.MessageResourceBundle.ERROR_MSG;
import static de.cofinpro.jsondb.server.config.MessageResourceBundle.OK_MSG;

/**
 * multi-thread safe key storage working with the file whose path is set as constant. Uses ReentrantReadWriteLock.
 */
@Slf4j
public class FileKeyStorage implements KeyStorage {

    private static final Path DB_PATH = Path.of("src/main/resources/server/data/db.json");
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    /**
     * reads database into a map, puts the key and writes it back. The writeLock is needed over the whole cycle (!) -
     * otherwise: 2 threads could read same time and set some new key, the first acquires lock and writes the first key,
     * then the second writes the second key and drops the first (because it is not read again)...
     */
    @Override
    public DatabaseResponse set(Object keys, Object value) {
        writeLock.lock();
        try {
            writeDatabase(putIntoDatabase(keys, value));
        } catch (IOException exception) {
            log.error("set {}: {} raised exception: {}", keys, value, exception);
        } finally {
            writeLock.unlock();
        }
        return DatabaseResponse.ok();
    }

    private Map<String, Object> putIntoDatabase(Object keys, Object value) throws IOException {
        List<String> keyList = extractListFrom(keys);
        var database = readDatabaseAsMap();
        Map<String, Object> parent = database;
        for (int i = 0; i < keyList.size() - 1; i++) {
            Map<String, Object> map = new HashMap<>();
            if (parent.get(keyList.get(i)) instanceof Map<?,?> keyMap) {
                safeFill(map, keyMap.entrySet());
            }
            parent.put(keyList.get(i), map);
            parent = map;
        }
        parent.put(keyList.get(keyList.size() - 1), value);
        return database;
    }

    private void safeFill(Map<String, Object> map, Set<? extends Map.Entry<?,?>> entrySet) {
        entrySet.forEach(entry -> {
            if (entry.getKey() instanceof String key) {
                map.put(key, entry.getValue());
            }
        });
    }

    private List<String> extractListFrom(Object keys) {
        if (keys instanceof String key) {
            return List.of(key);
        }
        List<String> result = new ArrayList<>();
        ((List<?>) keys).forEach(element -> {
            if (element instanceof String key) {
                result.add(key);
            }
        });
        return result;
    }

    /**
     * get only sets the readLock, that does not prevent other threads from simultaneous reads.
     */
    @Override
    public DatabaseResponse get(Object keys) {
        DatabaseResponse response;
        readLock.lock();
        try {
            var value = getFromDatabase(keys);
            response = value == null ? DatabaseResponse.error()
                    : new DatabaseResponse(OK_MSG, value, null);
        } catch (IOException exception) {
            log.error("get with key {}: raised exception: {}", keys, exception);
            response = new DatabaseResponse(ERROR_MSG, null, exception.getMessage());
        } finally {
            readLock.unlock();
        }
        return response;
    }

    private Object getFromDatabase(Object keys) throws IOException {
        Map<?,?> parent = readDatabaseAsMap();
        List<String> keyList = extractListFrom(keys);
        for (int i = 0; i < keyList.size() - 1; i++) {
            if (parent.get(keyList.get(i)) instanceof Map<?,?> keyMap) {
                parent = keyMap;
            } else {
                return null;
            }
        }
        return parent.get(keyList.get(keyList.size() - 1));
    }

    /**
     * delete needs writeLock of course. Same applies as stated in get.
     */
    @Override
    public DatabaseResponse delete(Object keys) {
        DatabaseResponse response;
        writeLock.lock();
        try {
            var dataBase = readDatabaseAsMap();
            response = removeFromDatabase(dataBase, keys) == null ? DatabaseResponse.error() : DatabaseResponse.ok();
            writeDatabase(dataBase);
        } catch (IOException exception) {
            log.error("delete {}: raised exception: {}", keys, exception);
            response = new DatabaseResponse(ERROR_MSG, null, exception.getMessage());
        } finally {
            writeLock.unlock();
        }
        return response;
    }

    private Object removeFromDatabase(Map<String, Object> dataBase, Object keys) {
        Map<?,?> parent = dataBase;
        List<String> keyList = extractListFrom(keys);
        for (int i = 0; i < keyList.size() - 1; i++) {
            if (parent.get(keyList.get(i)) instanceof Map<?,?> keyMap) {
                parent = keyMap;
            } else {
                return null;
            }
        }
        return parent.remove(keyList.get(keyList.size() - 1));
    }

    private Map<String, Object> readDatabaseAsMap() throws IOException {
        return GsonPooled.getGson()
                .fromJson(Files.readString(DB_PATH), new TypeToken<Map<String, Object>>(){}.getType());
    }

    private void writeDatabase(Map<String, Object> dataBase) throws IOException {
        try (OutputStream output = Files.newOutputStream(DB_PATH)) {
            output.write(GsonPooled.getGson().toJson(dataBase).getBytes());
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
