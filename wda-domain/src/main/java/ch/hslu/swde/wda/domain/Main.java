/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.domain;

import ch.hslu.swde.wda.restServer.RestServer;
import ch.hslu.swde.wda.rmiServer.RmiDataProviderImpl;
import ch.hslu.swde.wda.rmiServer.RmiDataProviderInterface;
import ch.hslu.swde.wda.database.DatabaseConnector;
import ch.hslu.swde.wda.reader.WeatherdataReader;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

/**
 *
 * @author Cedric
 */
public class Main {

    final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        // Start RMI Server
        try {
            RmiDataProviderInterface test1 = new RmiDataProviderImpl();
            Registry reg = LocateRegistry.createRegistry(1099);
            if (reg != null) {
                reg.rebind(RmiDataProviderInterface.RO_NAME, test1);

                String ip = "";
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (java.net.UnknownHostException ex) {
                    LOG.error(ex.getMessage());
                }
                LOG.info("RMI-Server started on " + ip + ":" + 1099);
            }
        } catch (RemoteException ex){
            LOG.error("Error while starting RMI Server! " + ex.getMessage());
            System.exit(0);
        }


        // Create new DB Object and connect to DB. Only used to see if connection is possible
        DatabaseConnector dbConn = new DatabaseConnector();
        dbConn.connect();

        // Start REST Service
        RestServer restserver = new RestServer();
        
        try {
            restserver.start();
        } catch (IOException ex) {
            LOG.error("Error while starting Rest Web Server! " + ex.getMessage());
            System.exit(0);
        }
        
        
        // Start thread which updates DB every hour
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> updateDB(), 0, 1, TimeUnit.HOURS);

        LOG.info("Server init finished");
    }

    private static void updateDB(){
        LOG.info("Updating database with new values");
        WeatherdataReader restReader = new WeatherdataReader();
        restReader.insertCitiesIntoDB();
        for (Object o : restReader.getAllCities()) {
            JSONObject city = (JSONObject) o;
            restReader.insertWeatherDataIntoDB(city.getString("name").replace(" ", "%20"));
        }
        LOG.info("Finished updating database");
    }

}
