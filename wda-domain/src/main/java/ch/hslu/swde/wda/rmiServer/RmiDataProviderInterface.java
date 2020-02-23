/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.rmiServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Cedric
 */
public interface RmiDataProviderInterface extends Remote {
    String getWeatherDataDB(int abfrageID, String abfrageString) throws RemoteException;
    
    String getCitiesDataDB() throws RemoteException;
    
    String checkLoginDataDB(String loginData_str) throws RemoteException;

    String checkSessionDataDB(String sesionData_str) throws RemoteException;
    
    String getUserDataDB() throws RemoteException;

    void addUserDB(String userData_str) throws RemoteException;

    void editUserDB(String userData_str) throws RemoteException;

    void deleteUserDB(String userData_str) throws RemoteException;

    String getCustomersDataDB()throws RemoteException;

    void addCustomerDB(String customerData_str)throws RemoteException;

    void editCustomerDB(String customerData_str)throws RemoteException;

    void deleteCustomerDB(String customerData_str)throws RemoteException;

    String RO_NAME = "DATAPROVIDER";
}
