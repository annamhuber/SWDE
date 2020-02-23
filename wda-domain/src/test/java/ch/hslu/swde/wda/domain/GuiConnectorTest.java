/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.domain;



import org.junit.jupiter.api.Test;


/**
 *
 * @author Cedric
 */
final class GuiConnectorTest {
    @Test
    public void testGetWeatherDataEmpty() {
        //When no data is in JSON Array, return empty one
        
        /*
        JSONArray dataarray = new JSONArray("[]");  
        JSONArray emptyarray = new JSONArray();       
        
        GuiConnector guiconnector;
        try {
            guiconnector = new GuiConnector();
                    JSONArray test1 = guiconnector.getWeatherData(2,dataarray);   
        
        assertEquals(test1.toString(), emptyarray.toString());
        } catch (ConnectException ex) {
            Logger.getLogger(GuiConnectorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
    
    @Test
    public void testGetWeatherDataWrongAbfrageID() {
        //When AbfrageID is not valid, return empty JSON Array
        /*
        JSONArray dataarray = new JSONArray("[data]");
        JSONArray emptyarray = new JSONArray();    
        
        GuiConnector guiconnector;
        try {
            guiconnector = new GuiConnector();
                   JSONArray test1 = guiconnector.getWeatherData(15,dataarray);   
        
        assertEquals(test1.toString(), emptyarray.toString());
        } catch (ConnectException ex) {
            Logger.getLogger(GuiConnectorTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }

}
