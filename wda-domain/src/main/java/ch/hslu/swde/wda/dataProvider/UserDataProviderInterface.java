/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.dataProvider;

import org.json.JSONArray;

/**
 *
 * @author Cedric
 */
public interface UserDataProviderInterface {
    JSONArray checkLoginData(JSONArray loginData);
    
    JSONArray checkSessionData(JSONArray loginData);
    
    JSONArray getUserDataRequest();
    
    public void addUser(JSONArray userData);
    
    public void editUser(JSONArray userData);
    
    public void deleteUser(JSONArray userData);
}
