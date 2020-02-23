/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.dataProvider;

import ch.hslu.swde.wda.database.DatabaseConnector;
import static ch.hslu.swde.wda.dataProvider.WeatherDataProviderImpl.LOG;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Cedric
 */
public class UserDataProviderImpl {

    /**
     * Compares user login input with the referenced name and password in the database
     * @param loginData Username and Password: input from GUI
     * @return 0 or 1 for Success or Failure and a Session, both in the same JSONObject in JSONArray
     */
    public JSONArray checkLoginData(JSONArray loginData){   
        JSONArray queryData = new JSONArray();
        JSONObject currentObject = loginData.getJSONObject(0); //Get first object from Array. Only one there
        String passwordUI = currentObject.getString("password");
        String usernameUI = currentObject.getString("username");
        System.out.println("Checking Login. Username: " + usernameUI + " Password: " + passwordUI);
        
        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT userID, username, password, session FROM login WHERE username = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usernameUI);
            
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                // Data for user
                int userID_DB = rs.getInt("userID");
                String username_DB = rs.getString("username");
                String password_DB = rs.getString("password");
                String session_DB = rs.getString("session");
                LOG.info("User '" + usernameUI + "' exists. Checking DB Data: " + userID_DB + "-" + username_DB + "-" + password_DB + "-" + session_DB);
                if(password_DB.equals(passwordUI)){
                    //Login correct
                    LOG.info("Password match. Login correct.");
                    JSONObject obj = new JSONObject();
                    obj.put("loginCorrect", 1);
                    obj.put("userID", userID_DB);
                    obj.put("session", session_DB);
                    queryData.put(obj);
                }else{
                    //Login wrong
                    LOG.info("Password mismatch. Login wrong");
                    JSONObject obj = new JSONObject();
                    obj.put("loginCorrect", 0);
                    queryData.put(obj);
                }
            }else{
                // No data for user
                LOG.info("No Data for that user! (Doesn't exist in DB)");
                JSONObject obj = new JSONObject();
                obj.put("loginCorrect", 0);
                queryData.put(obj);
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }
        return queryData;
        
    }

    /**
     * Compares session-data from UI with session data in database for a given user logging in.
     * @param loginData JSONArray containing session and userID-Strings
     * @return JSONArray containing 0 for failure and 1 for success
     */
    public JSONArray checkSessionData(JSONArray loginData){   
        JSONArray queryData = new JSONArray();
        JSONObject currentObject = loginData.getJSONObject(0); //Get first object from Array. Only one there
        String session_UI = currentObject.getString("session");
        String userID_UI = currentObject.getString("userID");
        System.out.println("Checking Session. Session: " + session_UI + " UserID : " + userID_UI);
        
        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT session, userID FROM login WHERE userID = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userID_UI);
            
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                String session_DB = rs.getString("session");
                //int userID_DB = rs.getInt("userID");
                
                if(session_DB.equals(session_UI)){
                    LOG.info("Session match. Session correct.");
                    JSONObject obj = new JSONObject();
                    obj.put("sessionCorrect", 1);
                    queryData.put(obj);
                }else{
                    LOG.info("Session mismatch. Session wrong.");  
                    JSONObject obj = new JSONObject();
                    obj.put("sessionCorrect", 0);
                    queryData.put(obj);
                }
            }else{
                LOG.info("No Data for that user! (Doesn't exist in DB)");
                JSONObject obj = new JSONObject();
                obj.put("sessionCorrect", 0);
                queryData.put(obj);
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }                
        return queryData;
    }
    /**
     * Reads all users, their ID and passwords from database and returns them
     * @return JSONArray containing all Users and Passwords + IDs
     */
    public JSONArray getUserDataRequest(){
        JSONArray queryData = new JSONArray();

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT username, password, userID FROM login";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("userID", rs.getInt("userID"));
                obj.put("username", rs.getString("username"));
                obj.put("password", rs.getString("password"));
                queryData.put(obj);
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }
        LOG.info("Fetched All Userdata from DB");
        return queryData;
    }

    /**
     * Adds specified User and Password to DB
     * @param userData Username and Password to add: input from GUI
     */
    public void addUser(JSONArray userData){
        JSONObject currentObject = userData.getJSONObject(0); //Get first object from Array.
        String password = currentObject.getString("password");
        String action = currentObject.getString("action");
        String username = currentObject.getString(("username"));
        String session = UUID.randomUUID().toString();
        LOG.info("Adding User: " + username);

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            //TODO:WHAT DO WHEN NO DBCONNECTION HALP CAN RETURN VOID
        }

        String sql = "INSERT INTO login (username, password, session) "
                + "VALUES(?, ?, ?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, session);
            
            int rs = pstmt.executeUpdate();

            if (rs != 0){
                LOG.info("Successfully updated " + rs + " rows");
                LOG.info("Added User Successfully");
                LOG.info("username: " + username);
                LOG.info("password: " + password);
                LOG.info("session: " + session);
            }
            else{
                LOG.info("Update not successful, could not add User");
            }
            conn.close();
        }
        catch(NullPointerException | SQLException ex){
            LOG.error("Error trying to update user: " + ex.getMessage());
        }
    }

    /**
     * Edits name and/or password of specified existing User in DB
     * Queries and outputs old and newly set username and password
     * @param userData New Username, new Password and userID to modify: input from GUI
     */
    public void editUser(JSONArray userData){
        JSONObject currentObject = userData.getJSONObject(0); //Get first object from Array.
        String password = currentObject.getString("password");
        String username = currentObject.getString("username");
        int id = currentObject.getInt("userID");
        LOG.info("Modifying User: " + id);

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
        }

        String sqlread = "SELECT username, password "
                + "FROM login "
                + "WHERE userID = ?";

        String sqlwrite = "UPDATE login "
                + "SET username = ?, password = ? "
                + "WHERE userID = ?";

        try {
            PreparedStatement pstmt_read = conn.prepareStatement(sqlread);
            pstmt_read.setInt(1, id);
            ResultSet rs = pstmt_read.executeQuery();
            String username_old = rs.getString("username");
            String password_old = rs.getString("password");
            
            PreparedStatement pstmt_write = conn.prepareStatement(sqlwrite);
            pstmt_write.setString(1, username);
            pstmt_write.setString(2, password);
            pstmt_write.setInt(3, id);
            int answer = pstmt_write.executeUpdate();

            if (answer != 0){
                LOG.info("Successfully updated " + answer + " rows");
                LOG.info("Old username: " + username_old + " New username: " + username);
                LOG.info("Old password: " + password_old + " New password: " + password);
            }
            else{
                LOG.info("Update not successful, could not modify User");
            }
            conn.close();
        }
        catch(NullPointerException | SQLException ex){
            LOG.error("Error trying to update user: " + ex.getMessage());
        }
    }

    /**
     * Deletes specified User by ID from DB
     * @param userData UserID to delete: input from GUI
     */
    public void deleteUser(JSONArray userData){
        JSONObject currentObject = userData.getJSONObject(0); //Get first object from Array.
        String action = currentObject.getString("action");
        int id = currentObject.getInt("userID");
        LOG.info("Deleting User: " + id);


        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
        }

        String sqlwrite = "DELETE FROM login "
                + "WHERE userID = ?";

        String sqlread = "SELECT username, userID "
                + "FROM login "
                + "WHERE userID = ?";

        try {
            PreparedStatement pstmt_read = conn.prepareStatement(sqlread);
            pstmt_read.setInt(1, id);
            ResultSet rs = pstmt_read.executeQuery();
            String username = rs.getString("username");
            int userID = rs.getInt("userID");

            PreparedStatement pstmt_write = conn.prepareStatement(sqlwrite);
            pstmt_write.setInt(1, id);
            int answer = pstmt_write.executeUpdate();

            if (answer != 0){
                LOG.info("Successfully deleted " + answer + " rows");
                LOG.info("deleted user: " + username + "with ID: " + userID);
            }
            else{
                LOG.info("Delete not successful, could not delete User");
            }
            conn.close();
        }
        catch(NullPointerException | SQLException ex){
            LOG.error("Error trying to update user: " + ex.getMessage());
        }

    }
}
