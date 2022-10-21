package de.cofinpro.jsondb.server.model;

import de.cofinpro.jsondb.io.json.DatabaseResponse;

import static de.cofinpro.jsondb.server.config.MessageResourceBundle.OK_MSG;

/**
 * mapper that maps the String/long responses of Redis (received via Jedis) to the DatabaseResponse
 * objects who are Jsonified to the SocketResponses.
 */
public class RedisResponseMapper {

    /**
     * Redis/Jedis-String response mapper
     */
    public DatabaseResponse toResponse(String redisResponse) {
        if (redisResponse == null) {
            return DatabaseResponse.error();
        }
        return redisResponse.equals(OK_MSG) ? DatabaseResponse.ok()
                : new DatabaseResponse(OK_MSG, redisResponse, null);
    }

    /**
     * Redis/Jedis-long response mapper (for del-command responses)
     */
    public DatabaseResponse toResponse(long redisDelResponse) {
        return redisDelResponse == 0L ? DatabaseResponse.error() : DatabaseResponse.ok();
    }
}
