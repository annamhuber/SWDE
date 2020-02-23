/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.ui;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Cedric
 */
public class WebServerIT {


    /**
     * Checks if server is startable
     */
    @Test
    public void testWebServerStart(){
        boolean thrown = false;
        WebServer server = new WebServer();
        try {
            server.start();
        } catch (IOException ioe){
            thrown = true;
        }
        assertFalse(thrown);
    }


    /**
     * Checks if all files from filesystem are there and not empty.
     */
    @Test
    public void testAccessFile(){
        WebServer server = new WebServer();
        
        String[] filepaths = { 
            "webUI/wetterdaten.html",
            "webUI/wetterdaten.js",
            "webUI/socket.js",
            "webUI/login.js",
            "webUI/login.html",
            "webUI/benutzer.html",
            "webUI/benutzer.js",
            "webUI/kunde.html",
            "webUI/kunde.js",
            "webUI/wetterdaten.html"
        };
        
        int i;
        for (i = 0; i < filepaths.length; i++){
            // static shouldn't be accessed like this 
            InputStream data = server.accessFile(filepaths[i]);
            String datastring = "";
            try {
                datastring = IOUtils.toString(data ,"UTF-8");
            } catch (IOException | NullPointerException ex) {
                fail("accessFile fail. File: " + filepaths[i]);
            }
            if(datastring.isEmpty()){
                fail("File: " + filepaths[i] + " empty");
            }
        }
    }

}
