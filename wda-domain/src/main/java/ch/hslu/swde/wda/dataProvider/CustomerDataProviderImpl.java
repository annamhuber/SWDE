package ch.hslu.swde.wda.dataProvider;

import ch.hslu.swde.wda.database.DatabaseConnector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.UUID;

import static ch.hslu.swde.wda.dataProvider.WeatherDataProviderImpl.LOG;


public class CustomerDataProviderImpl implements CustomerDataProviderInterface {

    /**
     * Gets connection-object, reads every customer and its data from the database.
     * @return JSONArray containing all customers, as well as corresponding data and IDs
     */
    @Override
    public JSONArray getCustomersDataRequest(){
        JSONArray queryData = new JSONArray();

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT kundeID, name, data FROM kunde";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("kundeid", rs.getInt("kundeID"));
                obj.put("name", rs.getString("name"));
                obj.put("data", rs.getString("data"));
                queryData.put(obj);
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }
        LOG.info("Fetched All CustomerData from DB");
        return queryData;
    }

    /**
     * Gets connection object and adds a new customer with the supplied data from GUI to the database
     * @param customerData JSONArray containing name and data of new customer to add to the database
     */
    @Override
    public void addCustomer(JSONArray customerData){
        JSONObject currentObject = customerData.getJSONObject(0); //Get first object from Array.
        String customername = currentObject.getString(("name"));
        String customerdata = currentObject.getString("data");
        String session = UUID.randomUUID().toString();
        LOG.info("Adding Customer: " + customername);

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            //TODO:WHAT DO WHEN NO DBCONNECTION HALP CAN RETURN VOID
        }

        String sql = "INSERT INTO kunde (name, data) "
                + "VALUES(?, ?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customername);
            pstmt.setString(2, customerdata);

            int rs = pstmt.executeUpdate();

            if (rs != 0){
                LOG.info("Successfully updated " + rs + " rows");
                LOG.info("Added Customer Successfully");
                LOG.info("name: " + customername);
                LOG.info("data: " + customerdata);
            }
            else{
                LOG.info("Update not successful, could not add Customer");
            }
            conn.close();
        }
        catch(NullPointerException | SQLException ex){
            LOG.error("Error trying to update Customer: " + ex.getMessage());
        }
    }

    /**
     * Gets connection-object and writes the supplied modified name / data to the referenced customer ID in the database
     * @param customerData JSONArray containing id, the modified name and data of existing customer to edit
     */
    @Override
    public void editCustomer(JSONArray customerData){
        JSONObject currentObject = customerData.getJSONObject(0); //Get first object from Array.
        String customername = currentObject.getString("name");
        String customerdata = currentObject.getString("data");
        int id = currentObject.getInt("kundeID");
        LOG.info("Modifying Customer: " + id);

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
        }

        String sqlread = "SELECT name, data "
                + "FROM kunde "
                + "WHERE kundeID = ?";

        String sqlwrite = "UPDATE kunde "
                + "SET name = ?, data = ? "
                + "WHERE kundeID = ?";

        try {
            PreparedStatement pstmt_read = conn.prepareStatement(sqlread);
            pstmt_read.setInt(1, id);
            ResultSet rs = pstmt_read.executeQuery();
            String customername_old = rs.getString("name");
            String customerdata_old = rs.getString("data");

            PreparedStatement pstmt_write = conn.prepareStatement(sqlwrite);
            pstmt_write.setString(1, customername);
            pstmt_write.setString(2, customerdata);
            pstmt_write.setInt(3, id);
            int answer = pstmt_write.executeUpdate();

            if (answer != 0){
                LOG.info("Successfully updated " + answer + " rows");
                LOG.info("Old Customer name: " + customerdata_old + " New Customer name: " + customername);
                LOG.info("Old Data: " + customerdata_old + " New Data: " + customerdata);
            }
            else{
                LOG.info("Update not successful, could not modify Customer");
            }
            conn.close();
        }
        catch(NullPointerException | SQLException ex){
            LOG.error("Error trying to update Customer: " + ex.getMessage());
        }
    }

    /**
     * Gets connection-object and deletes the referenced customer ID in the database
     * @param customerData JSONArray containing the ID of the customer to be deleted
     */
    @Override
    public void deleteCustomer(JSONArray customerData){
        JSONObject currentObject = customerData.getJSONObject(0); //Get first object from Array.
        int id = currentObject.getInt("kundeID");
        LOG.info("Deleting Customer: " + id);


        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
        }

        String sqlwrite = "DELETE FROM kunde "
                + "WHERE kundeID = ?";

        String sqlread = "SELECT name, data, kundeID "
                + "FROM kunde "
                + "WHERE kundeID = ?";

        try {
            PreparedStatement pstmt_read = conn.prepareStatement(sqlread);
            pstmt_read.setInt(1, id);
            ResultSet rs = pstmt_read.executeQuery();
            String customername = rs.getString("name");
            int customerID = rs.getInt("kundeID");

            PreparedStatement pstmt_write = conn.prepareStatement(sqlwrite);
            pstmt_write.setInt(1, id);
            int answer = pstmt_write.executeUpdate();

            if (answer != 0){
                LOG.info("Successfully deleted " + answer + " rows");
                LOG.info("deleted Customer: " + customername + "with ID: " + customerID);
            }
            else{
                LOG.info("Delete not successful, could not delete Customer");
            }
            conn.close();
        }
        catch(NullPointerException | SQLException ex){
            LOG.error("Error trying to update Customer: " + ex.getMessage());
        }

    }
}
