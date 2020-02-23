/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.rmiServer;
import ch.hslu.swde.wda.dataProvider.CustomerDataProviderImpl;
import ch.hslu.swde.wda.dataProvider.WeatherDataProviderImpl;
import ch.hslu.swde.wda.dataProvider.UserDataProviderImpl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.json.JSONArray;
        
/**
 *
 * @author Cedric
 */
public class RmiDataProviderImpl extends UnicastRemoteObject implements RmiDataProviderInterface {
    // Gets the Data from database with the DatabaseProvider Class.
    // Data from DatabaseProvider is stored in JSONArray or JSONObject, it is converted to String here to serialize it.

    private static final long serialVersionUID = -6802722834683209213L;

    WeatherDataProviderImpl dbWeatherData = new WeatherDataProviderImpl();
    UserDataProviderImpl dbUserData = new UserDataProviderImpl();
    CustomerDataProviderImpl dbCustomerData = new CustomerDataProviderImpl();
    
    public RmiDataProviderImpl() throws RemoteException {}

    /**
     * Returns data of all cities in String-format read from database
     * @return String object of JSONArray read from DB, containing all cities and their associated data
     * @throws RemoteException If there is a connection problem / method call problem
     */
    @Override
    public String getCitiesDataDB() throws RemoteException {
        // toString used to serialize.
        return dbWeatherData.getAllCities().toString();
    }

    /**
     * Switch-Case for different database-queries which expect different parameters depending on the case.
     * @param abfrageID Int determines which query-case to execute
     * @param abfrageString String containing parameters for the database query (date, city / cities)
     * @return String containing the queried weather data from the database
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public String getWeatherDataDB(int abfrageID, String abfrageString) throws RemoteException {
        JSONArray abfrageArray = new JSONArray(abfrageString);
        switch (abfrageID) {
            case 2:
                return dbWeatherData.getA02(abfrageArray).toString();
            case 3:
                return dbWeatherData.getA03(abfrageArray).toString();
            case 4: 
                return dbWeatherData.getA04(abfrageArray).toString();
            case 5:
                return dbWeatherData.getA05(abfrageArray).toString();
            case 6:
                return dbWeatherData.getA06(abfrageArray).toString();
            case 7:
                return dbWeatherData.getA07(abfrageArray).toString();
            case 8:
                return dbWeatherData.getA08(abfrageArray).toString();
            case 9:
                return dbWeatherData.getA09(abfrageArray).toString();
            case 10:
                return dbWeatherData.getA10(abfrageArray).toString();
            case 11:
                return dbWeatherData.getA11(abfrageArray).toString();
            case 12:
                return dbWeatherData.getA12(abfrageArray).toString();
            default:
                break;
        }
        return new JSONArray().toString(); // When no AbfrageID fits
    }

    /**
     * Queries every customer with corresponding ID and data from database
     * @return String containing all customers and corresponding ID and data from database
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public String getCustomersDataDB()throws RemoteException{
        return dbCustomerData.getCustomersDataRequest().toString();
    }

    /**
     * Adds a new customerID with supplied name and data from GUI to the db
     * @param customerData_str String containing new customer name and corresponding data
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public void addCustomerDB(String customerData_str)throws RemoteException{
        JSONArray sendeArray = new JSONArray(customerData_str);
        dbCustomerData.addCustomer(sendeArray);
    }

    /**
     * Edits an existing customers name and/or data referenced by ID
     * @param customerData_str String containing modified customer name and/or data as well as ID
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public void editCustomerDB(String customerData_str)throws RemoteException{
        JSONArray sendeArray = new JSONArray(customerData_str);
        dbCustomerData.editCustomer(sendeArray);
    }

    /**
     * Deletes an existing customer referenced by ID
     * @param customerData_str String containing ID of customer to delete
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public void deleteCustomerDB(String customerData_str)throws RemoteException{
        JSONArray sendeArray = new JSONArray(customerData_str);
        dbCustomerData.deleteCustomer(sendeArray);
    }

    /**
     * Queries every user with corresponding ID, password and name from db
     * @return String containing ID, password and name of every user in db
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public String getUserDataDB()throws RemoteException{
        return dbUserData.getUserDataRequest().toString();
    }

    /**
     * Supplies login data to method which cross-references between database and supplied data
     * @param loginData_str String containing supplied login data from GUI
     * @return String containing Success or Failure as 0 or 1
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public String checkLoginDataDB(String loginData_str) throws RemoteException {
        JSONArray sendeArray = new JSONArray(loginData_str);
        return dbUserData.checkLoginData(sendeArray).toString();
    }

    /**
     *
     * @param sessionData_str String containing session-data and ID of user whose session is checked
     * @return String containing Success or Failure as 0 or 1
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public String checkSessionDataDB(String sessionData_str) throws RemoteException {
        JSONArray sendeArray = new JSONArray(sessionData_str);
        return dbUserData.checkSessionData(sendeArray).toString();
    }

    /**
     * Method which calls method for database query and supplies userdata of new user to add
     * @param userData_str String containing username and password of new user to add
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public void addUserDB(String userData_str) throws RemoteException{
        JSONArray sendeArray = new JSONArray(userData_str);
        dbUserData.addUser(sendeArray);
    }

    /**
     * Method which calls method for database update and supplies updated userdata and referenced ID
     * @param userData_str String containing ID of user to modify as well as modified username and/or password
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public void editUserDB(String userData_str) throws RemoteException{
        JSONArray sendeArray = new JSONArray(userData_str);
        dbUserData.editUser(sendeArray);
    }

    /**
     * Method which calls method for database delete of supplied user ID
     * @param userData_str String containing user ID to supply
     * @throws RemoteException if remote object method call fails
     */
    @Override
    public void deleteUserDB(String userData_str) throws RemoteException{
        JSONArray sendeArray = new JSONArray(userData_str);
        dbUserData.deleteUser(sendeArray);
    }
}