package de.cofinpro.jsondb.server.model;

import java.util.Arrays;

import static de.cofinpro.jsondb.client.config.MessageResourceBundle.ERROR_MSG;
import static de.cofinpro.jsondb.client.config.MessageResourceBundle.OK_MSG;

/**
 * model class representing a fixed length string database, that wraps a String[].
 */
public class CellDatabase {

    private static final int DIMENSION = 100;

    private final String[] cells = new String[DIMENSION];

    public CellDatabase() {
        Arrays.fill(cells, "");
    }

    /**
     * set a value in the 'database'
     * @param index index to store into
     * @param text text to store
     * @return ERROR_MSG if index out of bound, OK_MSG else
     */
    public String set(int index, String text) {
        if (isInvalidIndex(index)) {
            return ERROR_MSG;
        }
        cells[index - 1] = text;
        return OK_MSG;
    }

    /**
     * delete a value in the 'database' by setting it to empty string
     * @param index index to delete
     * @return ERROR_MSG if index out of bound, OK_MSG else
     */
    public String delete(int index) {
        return set(index, "");
    }

    /**
     * get a value from the 'database' with given index
     * @param index index to return the database value for
     * @return ERROR_MSG if index out of bound or conten empty, the value else
     */
    public String get(int index) {
        if (isInvalidIndex(index) || cells[index - 1].isEmpty()) {
            return ERROR_MSG;
        }
        return cells[index - 1];
    }

    private boolean isInvalidIndex(int index) {
        return index < 1 || index > DIMENSION;
    }
}
