/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.ui;

import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Cedric
 */
public class WebServer extends NanoHTTPD {
    
    final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(WebServer.class);

    /**
    * Constructor which inits the webserver with port
    */
    public WebServer() {
        super(5000);
    }

    /**
    * Starts webServer
    * @throws IOException when connection is not possible
    */
    public void startServer() throws IOException {
        //Starts the webserver used to host GUI
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        LOG.info("Webserver started on localhost:8080");
    }

    /**
    * Serves data to web-Client depending on uri
    * @param session clientsession
    * @return Response with data 
    */
    @Override
    public Response serve(IHTTPSession session) {
        String response = "No Data";

        String uri=session.getUri(); //Get URI from browser
        switch (uri) {
            case "/wetterdaten":
                try {
                    response = IOUtils.toString(accessFile("webUI/wetterdaten.html"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/wetterdaten.js":
                try {
                    response = IOUtils.toString(accessFile("webUI/wetterdaten.js"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/socket.js":
                try {
                    response = IOUtils.toString(accessFile("webUI/socket.js"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/login.js":
                try {
                    response = IOUtils.toString(accessFile("webUI/login.js"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/login":
                try {
                    response = IOUtils.toString(accessFile("webUI/login.html"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/benutzer":
                try {
                    response = IOUtils.toString(accessFile("webUI/benutzer.html"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/benutzer.js":
                try {
                    response = IOUtils.toString(accessFile("webUI/benutzer.js"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/kundendaten":
                try {
                    response = IOUtils.toString(accessFile("webUI/kunde.html"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            case "/kunde.js":
                try {
                    response = IOUtils.toString(accessFile("webUI/kunde.js"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
            default:
                try {
                    response = IOUtils.toString(accessFile("webUI/wetterdaten.html"),"UTF-8");
                } catch (IOException ioe) {
                    LOG.info("Error serving file: " + ioe);
                }   break;
        }
        return newFixedLengthResponse(response);
    }
    
    /**
    * Returns HTML and CSS files from filesystem or JAR as InputStream
    * @param resource HTML or CSS file path
    * @return InputStream HTML or CSS data
    */
    public static InputStream accessFile(String resource) {
        // This function returns filedata, compatible with IDE and inside JAR.
        
        InputStream input = WebServer.class.getResourceAsStream("/resources/" + resource); // this is the path within the jar file
        
        if (input == null) {
            // When executed from inside an IDE
            input = WebServer.class.getClassLoader().getResourceAsStream(resource);
            //LOG.info("Served Data for IDE");
        }else{
            //LOG.info("Served Data for JAR");
        }
        return input;
    }
    
}
