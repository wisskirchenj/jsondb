package de.cofinpro.jsondb;

import com.google.gson.reflect.TypeToken;
import de.cofinpro.jsondb.client.controller.ClientController;
import de.cofinpro.jsondb.io.ConsolePrinter;
import de.cofinpro.jsondb.io.json.GsonPooled;
import de.cofinpro.jsondb.server.controller.ServerController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class ConcurrentFileKeyStorageAccessIT {

    // ServerSocket's maximum queue size for ACCEPT is 50 incoming connections, and any past that are blocked
    // https://stackoverflow.com/questions/10131377/how-to-set-maximum-queue-size-for-server-socket
    public static final int THREADS = 25;
    static ServerController server;
    static final Path DB_PATH = Path.of("src/main/resources/server/data/db.json");
    ClientController client = new ClientController(new ConsolePrinter());

    @BeforeAll
    static void setupServer() throws IOException {
        server = new ServerController(new ConsolePrinter());
        startServerThread();
        Files.writeString(DB_PATH, "{}");
    }

    @AfterAll
    static void closeServer() throws IOException {
        String[] args = new String[]{"-t", "exit"};
        new ClientController(new ConsolePrinter()).send(args);
    }


    static void startServerThread() {
        new Thread(() -> {
            try {
                server.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Test
    void whenManyConcurrentClientThreads_ReentrantReadWriteLockWorks() throws IOException, InterruptedException {
        Instant before = Instant.now();
        IntStream.rangeClosed(1, THREADS).forEach(n -> {
            startSetClient(n);
            startGetClients(n);
        });
        System.out.printf("Starting took %d milliseconds", Instant.now().toEpochMilli() - before.toEpochMilli());
        Thread.sleep(200);
        Map<String, String> database = GsonPooled.getGson().fromJson(Files.readString(DB_PATH),
                new TypeToken<Map<String, String>>(){}.getType());
        assertEquals(THREADS, database.size());
    }

    void startSetClient(int count) {
        Thread.ofVirtual().start(() -> {
            try {
                client.send(new String[] {"-t", "set", "-k", "test%d".formatted(count), "-v", String.valueOf(count)});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    void startGetClients(int count) {
        Thread.ofVirtual().start(() -> {
            try {
                client.send(new String[] {"-t", "get", "-k", "test%d".formatted(count)});
                client.send(new String[] {"-t", "get", "-k", "test1_%d".formatted(count)});
                client.send(new String[] {"-t", "get", "-k", "test2_%d".formatted(count)});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
