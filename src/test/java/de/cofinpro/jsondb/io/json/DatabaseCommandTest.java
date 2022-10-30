package de.cofinpro.jsondb.io.json;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseCommandTest {

    DatabaseCommand databaseCommand;

    @BeforeEach
    void setUp() {
        databaseCommand = new DatabaseCommand();
    }

    @Test
    void whenExitCommandArgs_DatabaseCommandFilledCorrect() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "exit");
        assertEquals("exit", databaseCommand.getType());
        assertNull(databaseCommand.getKey());
        assertNull(databaseCommand.getValue());
    }

    @Test
    void whenGetCommandArgs_DatabaseCommandFilledCorrect() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "get", "-k", "123");
        assertEquals("get", databaseCommand.getType());
        assertEquals(List.of("123"), databaseCommand.getKey());
        assertNull(databaseCommand.getValue());
    }

    @Test
    void whenSetCommandArgs_DatabaseCommandFilledCorrect() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "set", "-k", "123", "-v", "what a long message");
        assertEquals("set", databaseCommand.getType());
        assertEquals(List.of("123"), databaseCommand.getKey());
        assertEquals(List.of("what a long message"), databaseCommand.getValue());
    }

    @Test
    void whenSetCommandMsgWithMultiSpace_DatabaseCommandKeepsSpaces() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "set", "-v", "what a long   -message",  "-k", "456");
        assertEquals("set", databaseCommand.getType());
        assertEquals(List.of("456"), databaseCommand.getKey());
        assertEquals(List.of("what a long   -message"), databaseCommand.getValue());
    }
}