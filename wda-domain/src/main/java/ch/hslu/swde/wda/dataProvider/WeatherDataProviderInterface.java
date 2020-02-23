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
public interface WeatherDataProviderInterface {
    
    public JSONArray getAllCities();
    
    public JSONArray getA02(JSONArray abfrageArray);
    
    public JSONArray getA03(JSONArray abfrageArray);
    
    public JSONArray getA04(JSONArray abfrageArray);
    
    public JSONArray getA05(JSONArray abfrageArray);
    
    public JSONArray getA06(JSONArray abfrageArray);
    
    public JSONArray getA07(JSONArray abfrageArray);
            
    public JSONArray getA08(JSONArray abfrageArray);
    
    public JSONArray getA09(JSONArray abfrageArray);
    
    public JSONArray getA10(JSONArray abfrageArray);
    
    public JSONArray getA11(JSONArray abfrageArray);
    
    public JSONArray getA12(JSONArray abfrageArray);
}
