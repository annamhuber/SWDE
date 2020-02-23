/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.ui;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import java.net.BindException;
import java.rmi.ConnectException;
import org.apache.logging.log4j.LogManager;
import org.json.*;

/**
 *
 * @author Cedric
 */
public class SocketServer {
        
    final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(SocketServer.class);
    SocketIOServer server;
    RmiSocketioConnector rmiConnector; // Object used to send and get Data from RMI

    /**
    * Constructor which inits the socket.io server and adds listeners and emits
    * @throws ConnectException when connection is not possible
    * @throws BindException when Socket.io server can't get started
    */
    public SocketServer() throws ConnectException, BindException {
        
        // Config of socket.io Server
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);
        server = new SocketIOServer(config);

        
        server.addEventListener("checkLogin", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("checkLogin called from UI"); 

                JSONArray dbData = rmiConnector.checkLogin(data);
                
                if( dbData.getJSONObject(0).getInt("loginCorrect") == 1){
                    LOG.info("Login correct!");
                    server.getBroadcastOperations().sendEvent("loginUserCorrect", dbData.toString());
                } else {
                    LOG.info("Login wrong!");
                    server.getBroadcastOperations().sendEvent("loginUserWrong");
                }

            }
        }); 

        server.addEventListener("checkSession", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("checkSession called from UI"); 
                
                JSONArray dbData = rmiConnector.checkSession(data); // username, session, password
                
                if( dbData.getJSONObject(0).getInt("sessionCorrect") == 1){
                    server.getBroadcastOperations().sendEvent("sessionCorrect");
                } else {
                    server.getBroadcastOperations().sendEvent("sessionWrong");
                }
            }
        }); 
        
        server.addEventListener("getCitiesRequest", String[].class, new DataListener<String[]>() {
            @Override
            public void onData(SocketIOClient socketIOClient, String[] data, AckRequest ackRequest) {
                // noData!
                LOG.info("getCitiesRequest called from UI");                
                JSONArray dbData = rmiConnector.getCities();
                server.getBroadcastOperations().sendEvent("getCitiesResponse", dbData.toString());
            }
        });
                   
        server.addEventListener("getWeatherdataRequest", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("getWeatherdataRequest called from UI");  
                
                int abfrageID = data.getJSONObject(0).getInt("abfrageID"); //Get abfrageID from first Object
                JSONArray dbData = rmiConnector.getWeatherData(abfrageID, data); 
                switch (abfrageID) {
                    case 2:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA02", dbData.toString());
                        break;
                    case 3:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA03", dbData.toString());
                        break;
                    case 4:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA04", dbData.toString());
                        break;
                    case 5:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA05", dbData.toString());
                        break;
                    case 6:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA06", dbData.toString());
                        break;
                    case 7:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA07", dbData.toString());
                        break;
                    case 8:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA08", dbData.toString());
                        break;            
                    case 9:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA09", dbData.toString());
                        break;
                    case 10:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA10", dbData.toString());
                        break;
                    case 11:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA11", dbData.toString());
                        break;
                    case 12:
                        server.getBroadcastOperations().sendEvent("weatherdataResponseA12", dbData.toString());
                        break;
                }
            }
        });
        
        server.addEventListener("getUserDataRequest", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("getUserDataRequest called from UI");  
                JSONArray dbData = rmiConnector.getUserDataRequest();    
                server.getBroadcastOperations().sendEvent("getUserDataResponse", dbData.toString());
            }
        }); 
        
        server.addEventListener("addUser", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("addUser called from UI");  
                rmiConnector.addUser(data);    
            }
        }); 
        
        server.addEventListener("editUser", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("editUser called from UI");  
                rmiConnector.editUser(data);    
            }
        }); 
        
        server.addEventListener("deleteUser", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("deleteUser called from UI");  
                rmiConnector.deleteUser(data);    
            }
        }); 
        
        server.addEventListener("getCustomersDataRequest", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("getCustomersDataRequest called from UI ");  
                JSONArray dbData = rmiConnector.getCustomersDataRequest();    
                server.getBroadcastOperations().sendEvent("getCustomersDataResponse", dbData.toString());
            }
        }); 
        
        server.addEventListener("addCustomer", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("addCustomer called from UI ");  
                rmiConnector.addCustomer(data);    
            }
        }); 
        
        server.addEventListener("editCustomer", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("editCustomer called from UI ");  
                rmiConnector.editCustomer(data);    
            }
        }); 
        
        server.addEventListener("deleteCustomer", JSONArray.class, new DataListener<JSONArray>() {
            @Override
            public void onData(SocketIOClient socketIOClient, JSONArray data, AckRequest ackRequest) {
                LOG.info("deleteCustomer called from UI ");  
                rmiConnector.deleteCustomer(data);    
            }
        });      
    }

    /**
    * Starts the Socket.IO server
    * @throws ConnectException when connection to socket.io is not possible
    * @throws BindException when RMI server can't get started
    */
    public void startServer() throws ConnectException, BindException {
        server.start();
        LOG.info("socket.io Server started on localhost:9092");
        rmiConnector = new RmiSocketioConnector(); //Constructor starts RMI Client 
    }
    
    /**
    * Stops the Socket.IO server
    */
    public void stopServer() {
        server.stop();
        LOG.info("socket.io server stopped.");
    }
}
