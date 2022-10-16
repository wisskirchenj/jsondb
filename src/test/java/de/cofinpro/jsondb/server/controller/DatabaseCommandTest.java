package de.cofinpro.jsondb.server.controller;

import com.beust.jcommander.JCommander;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertEquals("exit", databaseCommand.getCommand());
        assertNull(databaseCommand.getCellIndex());
        assertNull(databaseCommand.getMessage());
    }

    @Test
    void whenGetCommandArgs_DatabaseCommandFilledCorrect() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "get", "-i", "123");
        assertEquals("get", databaseCommand.getCommand());
        assertEquals(123, databaseCommand.getCellIndex());
        assertNull(databaseCommand.getMessage());
    }

    @Test
    void whenSetCommandArgs_DatabaseCommandFilledCorrect() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "set", "-i", "123", "-m", "what", "a", "long", "message");
        assertEquals("set", databaseCommand.getCommand());
        assertEquals(123, databaseCommand.getCellIndex());
        assertEquals("what a long message", databaseCommand.getMessage());
    }

    @Test
    void whenSetCommandMsgWithMultiSpace_DatabaseCommandKeepsSpaces() {
        JCommander.newBuilder()
                .addObject(databaseCommand)
                .build()
                .parse("-t", "set", "-m", "what a long   -message",  "-i", "123");
        assertEquals("set", databaseCommand.getCommand());
        assertEquals(123, databaseCommand.getCellIndex());
        assertEquals("what a long   -message", databaseCommand.getMessage());
    }
}