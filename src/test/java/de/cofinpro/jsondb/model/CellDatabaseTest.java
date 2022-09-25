package de.cofinpro.jsondb.model;

import de.cofinpro.jsondb.config.MessageResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CellDatabaseTest {

    CellDatabase cellDatabase;

    @BeforeEach
    void setUp() {
        cellDatabase = new CellDatabase();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -1000, 101, 10000})
    void whenWrongIndex_setReturnsError(int index) {
        assertEquals(MessageResourceBundle.ERROR_MSG, cellDatabase.set(index, "sth"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 34, 100})
    void whenGoodIndex_setReturnsOk(int index) {
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.set(index, "sth long with  %$!"));
    }

    @Test
    void whenSetAsOverwrite_setReturnsOk() {
        int index = 10;
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.set(index, "sth"));
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.set(index, "sth new"));
        assertEquals("sth new", cellDatabase.get(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 100})
    void whenGoodIndex_setStoresValueAndGetRetrieves(int index) {
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.set(index, String.valueOf(index)));
        assertEquals(index, Integer.parseInt(cellDatabase.get(index)));
    }


    @ParameterizedTest
    @ValueSource(ints = {3, 5, 36, 99})
    void whenNothingStored_getReturnsError(int index) {
        assertEquals(MessageResourceBundle.ERROR_MSG, cellDatabase.get(index));
    }


    @ParameterizedTest
    @ValueSource(ints = {0, -1, -1000, 101, 10000})
    void whenWrongIndex_getReturnsError(int index) {
        assertEquals(MessageResourceBundle.ERROR_MSG, cellDatabase.get(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -1000, 101, 10000})
    void whenWrongIndex_deleteReturnsError(int index) {
        assertEquals(MessageResourceBundle.ERROR_MSG, cellDatabase.delete(index));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 36, 99})
    void whenNothingStored_deleteReturnsOk(int index) {
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.delete(index));
    }


    @ParameterizedTest
    @ValueSource(ints = {4, 22})
    void whenSomethingStored_deleteReturnsOkAndDeletes(int index) {
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.set(index, "something"));
        assertEquals(MessageResourceBundle.OK_MSG, cellDatabase.delete(index));
        assertEquals(MessageResourceBundle.ERROR_MSG, cellDatabase.get(index));
    }
}