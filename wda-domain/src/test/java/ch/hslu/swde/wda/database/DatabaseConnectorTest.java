package ch.hslu.swde.wda.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

/**
 *
 * @author Lukas
 */
class DatabaseConnectorTest {

    @Test
    void connect() {
        DatabaseConnector dbConn = new DatabaseConnector();
        try {
            Assertions.assertTrue(dbConn.connect().isValid(15));
        } catch (SQLException e) {
            Assertions.fail("DB connection is not valid or not reachable, check connection string.");
        }
    }
}