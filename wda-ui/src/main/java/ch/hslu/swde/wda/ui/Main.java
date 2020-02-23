/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.ConnectException;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Cedric
 */

public class Main {
    final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(Main.class);
    
    public static void main(String[] args) throws IOException {
        
        // A webserver and socket.io server is used to host the GUI from the client. Servers only used on client localhost       
        try {
            SocketServer socketsrv = new SocketServer();
            socketsrv.startServer();  // Starts Socket.io Server and RMI Client. RMI Client is started in RmiSocketioConnector
        } catch (ConnectException | RuntimeException | BindException ex){
            LOG.error("RMI Client Init failed! " + ex);
            System.exit(0);
        }
        
        try {
            WebServer websrv = new WebServer();
            websrv.startServer(); // Starts Webserver
        } catch(IOException | RuntimeException ex){
            LOG.error("webServer Init failed!" + ex);
            System.exit(0);
        }
         
        try {
            // Open Webbrowser directly to access GUI
            LOG.info("Opening GUI...");
            Desktop.getDesktop().browse(new URL("http://localhost:5000/wetterdaten").toURI());
        } catch (MalformedURLException | URISyntaxException ex) {
            LOG.error("Can't open Webpage! Error: " + ex);
        } 
        LOG.info("Client init finished.");
    }
}
