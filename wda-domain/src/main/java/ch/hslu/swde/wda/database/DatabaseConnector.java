/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.database;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to get a connection object which contains the path of the database file
 * to be used and is used to validate the connection to the database.
 *
 * @author Cedric
 */
public class DatabaseConnector {

    final static Logger LOG = LogManager.getLogger(DatabaseConnector.class);

    public DatabaseConnector() {

    }

    /**
     * Contains paths of the database Decides the value of 'url' to look for
     * database-file depending on how the program is run (JAR or IDE)
     *
     * @return Connection-object which contains path information for
     * database-connectivity
     */
    public Connection connect() {
        // SQLite connection string

        String url = "";

        File dbPathIDE = new File(System.getProperty("user.dir") + "/src/main/resources/wda_db.db");

        if (dbPathIDE.exists() && !dbPathIDE.isDirectory()) {
            // Load DB from resources folder when working inside IDE
            url = "jdbc:sqlite:src/main/resources/wda_db.db";
        } else {
            // From JAR. Expects path at same location as jar file.
            try {
                String path = DatabaseConnector.class.getProtectionDomain().getCodeSource().getLocation().getPath(); //Get path of jar
                path = path.substring(0, path.lastIndexOf("/") + 1); // remove name of jar from path
                
                String decodedPath = URLDecoder.decode(path, "UTF-8"); // Convert path to string
                decodedPath = decodedPath.substring(1); // Remove a / from the string in the beginning

                //Check if DB file is there
                File dbPathJAR = new File(decodedPath + "/wda_db.db");
                if (!dbPathJAR.exists()){
                   LOG.error("Database not found! Database file must be at the same location as JAR of server!");
                    System.exit(0);
                }
                
                url = "jdbc:sqlite:" + decodedPath + "/wda_db.db"; // Final path
            } catch (UnsupportedEncodingException ex) {
                LOG.error("Database not found! Database file must be at the same location as JAR of server!");
                System.exit(0);
            }
            
        }

        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            //LOG.info("Connected to SQLite Database.");
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            LOG.error("ClassNotFoundException: " + e.getMessage());
        }
        return conn;
    }
}
