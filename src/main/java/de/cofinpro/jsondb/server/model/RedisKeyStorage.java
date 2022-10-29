package de.cofinpro.jsondb.server.model;

import de.cofinpro.jsondb.io.json.DatabaseResponse;
import redis.clients.jedis.JedisPooled;

/**
 * Jedis-based key storage wrapper class, that opens a connection on instantiate to a standard parameter
 * (localhost, port 6379) Redis server, that must be up running before the Java application starts.
 */
public class RedisKeyStorage implements KeyStorage {

    private final JedisPooled jedis = new JedisPooled();
    private final RedisResponseMapper mapper = new RedisResponseMapper();

    @Override
    public DatabaseResponse set(Object key, Object value) {
        return mapper.toResponse(jedis.set(key.toString(), value.toString()));
    }

    @Override
    public DatabaseResponse get(Object key) {
        return mapper.toResponse(jedis.get(key.toString()));
    }

    @Override
    public DatabaseResponse delete(Object key) {
        return mapper.toResponse(jedis.del(key.toString()));
    }

    @Override
    public void close() {
        jedis.close();
    }
}
