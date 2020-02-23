/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.ui;

import java.rmi.ConnectException;
import org.json.JSONArray;

import org.apache.logging.log4j.LogManager;
/**
 *
 * @author Cedric
 */

public class RmiSocketioConnector {
    final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RmiSocketioConnector.class);
    
    RmiClient rmiClient = null;
    
    /**
    * Constructor which starts RMI connection.
    */
    public RmiSocketioConnector() {
        try {
            rmiClient = new RmiClient();
        } catch (ConnectException ex) {
            LOG.error("RMI Client init failed! " + ex.getMessage());
            System.exit(0);
        }
    }
    
    public JSONArray getCities(){
        JSONArray data = rmiClient.getCitiesDataDB();
        //LOG.info("GuiConnector - Sent CitiesData to GUI");
        return data;
    }
    
    public JSONArray checkLogin(JSONArray userData){   
        JSONArray data = rmiClient.checkLoginDB(userData);
        //LOG.info("GuiConnector - Sent loginData to GUI");
        return data;
    }
    
    public JSONArray checkSession(JSONArray userData){   
        //userData ExampleArray: [{"session":"43t34g34ggfh4wewr","userID":"1"}]
        // Returns: sessionCorrect 0 or 1
        JSONArray data = rmiClient.checkSessionDB(userData);
        return data;        
    }
    
    public JSONArray getUserDataRequest(){
        JSONArray data = rmiClient.getUserDataDB();
        //LOG.info("GuiConnector - Sent UserData_All to GUI");
        return data;
    }
    
    public void addUser(JSONArray userData){
        System.out.println(userData);
        rmiClient.addUserDB(userData);
        //LOG.info("GuiConnector - Sent addUserData to RmiClient");
    }
    
    public void editUser(JSONArray userData){
        System.out.println(userData);
        rmiClient.editUserDB(userData);
        //LOG.info("GuiConnector - Sent editUserData to RmiClient");
    }
        
    public void deleteUser(JSONArray userData){
        System.out.println(userData);
        rmiClient.deleteUserDB(userData);
        //LOG.info("GuiConnector - Sent deleteUserData to RmiClient");
    }

    public JSONArray getCustomersDataRequest(){
        JSONArray data = rmiClient.getCustomerDataDB();
        //LOG.info("GuiConnector - Sent CustomerData_All to GUI");
        return data;
    }

    public void addCustomer(JSONArray customerData){
        rmiClient.addCustomerDB(customerData);
        //LOG.info("GuiConnector - Sent addUserData to RmiClient");
    }

    public void editCustomer(JSONArray customerData){;
        rmiClient.editCustomerDB(customerData);
        //LOG.info("GuiConnector - Sent editCustomerData to RmiClient");
    }

    public void deleteCustomer(JSONArray customerData){
        rmiClient.deleteCustomerDB(customerData);
        //LOG.info("GuiConnector - Sent deleteCustomerData to RmiClient");
    }
    
    public JSONArray getWeatherData(int abfrageID, JSONArray selectedValues){
        if(selectedValues.isEmpty()){
            LOG.error("Received empty Abfrage-JSONArray from GUI");
            return new JSONArray();
        }
       
        if (abfrageID >= 2 && abfrageID <= 12){
            JSONArray data = rmiClient.getWeatherDataDB(abfrageID, selectedValues);
            //LOG.info("GuiConnector - Sent WeatherData to GUI");
            return data;
        } else{
            return new JSONArray(); // When no data
        }
    }
}