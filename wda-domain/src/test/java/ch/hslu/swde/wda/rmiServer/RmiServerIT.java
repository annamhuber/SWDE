/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.rmiServer;
import ch.hslu.swde.wda.rmiServer.RmiDataProviderImpl;
import ch.hslu.swde.wda.rmiServer.RmiDataProviderInterface;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.json.JSONArray;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
/**
 *
 * @author Cedric
 */
public class RmiServerIT {
        // Start RMI Server
    
    /**
     * Test for RmiDataProvider, inits server and client and looks if there is data
     */
    @Test
    public void testServerResponse(){
        
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
                    Assertions.fail(ex);
                }
            }
        } catch (RemoteException ex){
            Assertions.fail(ex);
        }
        
        
        //Start RMI Client
        RmiDataProviderInterface stub = null;
        String rmiServerIP = "127.0.0.1";
        int rmiPort = 1099;
        
        String url = "rmi://" + rmiServerIP + ":" + rmiPort + "/DATAPROVIDER";
        
        boolean istheredata = false;
        try {
            stub = (RmiDataProviderInterface) Naming.lookup(url);
            JSONArray data = new JSONArray(stub.getCitiesDataDB()); // getcitiesdatadb returns string
            if (!data.isEmpty()){
                istheredata = true;
            }
        } catch (MalformedURLException | NotBoundException | RemoteException ex) {
            Assertions.fail(ex);
        }
        
        assertTrue(istheredata);
    }
}
