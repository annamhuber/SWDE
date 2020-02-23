/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.ui;

import ch.hslu.swde.wda.rmiServer.RmiDataProviderInterface;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

/**
 *
 * @author Cedric
 */
public class RmiClient {

    final static Logger LOG = LogManager.getLogger(RmiClient.class);
    RmiDataProviderInterface stub = null;

    /**
    * Constructor which starts RMI Client
    * @throws ConnectException when connection is not possible
    */
    public RmiClient() throws ConnectException {
        String rmiServerIP = "127.0.0.1";
        int rmiPort = 1099;

        Scanner inputscanner = new Scanner(System.in);
        System.out.println("Enter RMI-Server IP! (Or press enter to connect to a localhost server)");
        String input = inputscanner.nextLine();
        if(!input.equals("")){
            // Warning: Scanner blocks all tests!
            // Use 127.0.0.1 as IP when user just pressed enter
            rmiServerIP = input;
        }
        
        String url = "rmi://" + rmiServerIP + ":" + rmiPort + "/DATAPROVIDER";

        try {
            stub = (RmiDataProviderInterface) Naming.lookup(url);
        } catch (MalformedURLException | NotBoundException | RemoteException ex) {
            throw new ConnectException("Error: " + ex);
        }
        LOG.info("RMI Client running. Connected to " + url);
    }

    /**
    * Gets all Cities over RMI stub
    * @return JSONArray with all Cities in JSONObjects
    */
    public JSONArray getCitiesDataDB() {
        try {
            LOG.info("RMI Client - Sent CitiesData request to RMI Server.");
            JSONArray data = new JSONArray(stub.getCitiesDataDB()); // getcitiesdatadb returns string
            LOG.info("RMI Client - Got CitiesData response from RMI Server.");
            return data;
        } catch (RemoteException rex) {
            LOG.error("RMI Client - getCitiesDataDB error: " + rex);
        }
        return new JSONArray(); // when error happens
    }

    /**
    * Gets WeatherData for specific Abfrage over RMI stub
    * @param abfrageID 
    * @param abfrageArray JSONArray which ontains one or more JSONObjects with Startdate, Enddate, CityID
    * @return JSONArray with weatherdata 
    */
    public JSONArray getWeatherDataDB(int abfrageID, JSONArray abfrageArray) {
        String abfrageString = abfrageArray.toString();
        try {
            LOG.info("RMI Client - Sent WeatherData request to RMI Server.");
            JSONArray data = new JSONArray(stub.getWeatherDataDB(abfrageID, abfrageString));
            LOG.info("RMI Client - Got WeatherData response from RMI Server.");
            return data;
        } catch (RemoteException rex) {
            LOG.error("RMI Client - getWeatherData error: " + rex);
        }
        return new JSONArray(); //when error happens return empty
    }
    
    /**
    * Checks if Logindata is correct over RMI stub
    * @param userData contains username and password which was entered in GUI
    * @return JSONArray with successdata, session
    */
    public JSONArray checkLoginDB(JSONArray userData){
        String userData_str = userData.toString();
        try{
            LOG.info("RMI Client - Send checkLogin request to RMI Server.");
            JSONArray data = new JSONArray(stub.checkLoginDataDB(userData_str));
            LOG.info("RMI Client - Got checkLogin response from RMI Server.");
            return data;
        } catch(RemoteException rex){
            LOG.error("RMI Client - addUser error: " + rex);
        }
        return new JSONArray(); // when error happens
    }
    
    /**
    * Checks if session is correct over RMI stub
    * @param userData contains session and userid from GUI cookies
    * @return JSONArray with successdata 
    */
    public JSONArray checkSessionDB(JSONArray userData){
        String userData_str = userData.toString();
        try{
            LOG.info("RMI Client - Send checkSession request to RMI Server.");
            JSONArray data = new JSONArray(stub.checkSessionDataDB(userData_str));
            LOG.info("RMI Client - Got checkSession response from RMI Server.");
            return data;
        } catch(RemoteException rex){
            LOG.error("RMI Client - checkSessionDB error: " + rex);
        }
        return new JSONArray(); // when error happens
    }

    /**
    * Gets all Users over RMI stub
    * @return JSONArray with JSONObjects which contain the users
    */
    public JSONArray getUserDataDB() {
        try {
            LOG.info("RMI Client - Sent getUserData request to RMI Server.");
            JSONArray data = new JSONArray(stub.getUserDataDB()); // getUserDataDB returns string
            LOG.info("RMI Client - Got getUserData response from RMI Server.");
            return data;
        } catch (RemoteException rex) {
            LOG.error("RMI Client - getUserData error: " + rex);
        }
        return new JSONArray(); // when error happens
    }

    /**
    * Adds an user over RMI stub
    * @param userData JSONArray which contains one JSONObject with username and password
    */
    public void addUserDB(JSONArray userData){
        String userData_str = userData.toString();
        try{
            LOG.info("RMI Client - Sent addUser request to RMI Server.");
            stub.addUserDB(userData_str);
        } catch(RemoteException rex){
            LOG.error("RMI Client - addUser error: " + rex);
        }
    }

    /**
    * Edits an user over RMI stub
    * @param userData JSONArray which contains one JSONObject with username, userid and password
    */
    public void editUserDB(JSONArray userData){
        String userData_str = userData.toString();
        try{
            LOG.info("RMI Client - Sent editUser request to RMI Server.");
            stub.editUserDB(userData_str);
            
        } catch (RemoteException rex){
            LOG.error("RMI Client - editUser error: " + rex);
        }
    }

    /**
    * Deletes an user over RMI stub
    * @param userData JSONArray which contains one JSONObject with username, userid and password
    */
    public void deleteUserDB(JSONArray userData){
        String userData_str = userData.toString();
        try{
            LOG.info("RMI Client - Sent deleteUser request to RMI Server.");
            stub.deleteUserDB(userData_str);
        } catch (RemoteException rex){
            LOG.error("RMI Client - deleteUser error: " + rex);
        }
    }

    /**
    * Gets all Customers over RMI stub
    * @return JSONArray with JSONObjects which contain the users
    */
    public JSONArray getCustomerDataDB() {
        try {
            LOG.info("RMI Client - Sent getCustomerData request to RMI Server.");
            JSONArray data = new JSONArray(stub.getCustomersDataDB()); // getUserDataDB returns string
            LOG.info("RMI Client - Got getCustomerData response from RMI Server.");
            return data;
        } catch (RemoteException rex) {
            LOG.error("RMI Client - getCustomer error: " + rex);
        }
        return new JSONArray(); // when error happens
    }

    /**
    * Adds an customer over RMI stub
    * @param customerData JSONArray which contains one JSONObject with name and data
    */
    public void addCustomerDB(JSONArray customerData){
        String customerData_str = customerData.toString();
        try{
            LOG.info("RMI Client - Sent addCustomer request to RMI Server.");
            stub.addCustomerDB(customerData_str);
        } catch(RemoteException rex){
            LOG.error("RMI Client - addCustomer error: " + rex);
        }
    }
    
    /**
    * Edits an customer over RMI stub
    * @param customerData JSONArray which contains one JSONObject with customerid, name and data
    */
    public void editCustomerDB(JSONArray customerData){
        String customerData_str = customerData.toString();
        try{
            LOG.info("RMI Client - Sent editCustomer request to RMI Server.");
            stub.editCustomerDB(customerData_str);
        } catch (RemoteException rex){
            LOG.error("RMI Client - editCustomer error: " + rex);
        }
    }

    /**
    * Deletes an customer over RMI stub
    * @param customerData JSONArray which contains one JSONObject with customerid, name and data
    */
    public void deleteCustomerDB(JSONArray customerData){
        String customerData_str = customerData.toString();
        try{
            LOG.info("RMI Client - Sent deleteCustomer request to RMI Server.");
            stub.deleteCustomerDB(customerData_str);
        } catch (RemoteException rex){
            LOG.error("RMI Client - deleteCustomer error: " + rex);
        }
    }
}
