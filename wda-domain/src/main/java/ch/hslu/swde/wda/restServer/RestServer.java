/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.hslu.swde.wda.restServer;

import ch.hslu.swde.wda.dataProvider.WeatherDataProviderImpl;

import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Cedric
 */

public class RestServer extends NanoHTTPD {

    final static org.apache.logging.log4j.Logger LOG = LogManager.getLogger(RestServer.class);

     /**
     * Constructor which inits port and server
     */
    public RestServer() {
        super(8080); // port
        LOG.info("REST WebService started on localhost:8080");
    }

    /**
     * starts RestServer
     * @throws IOException
     */
    public void startServer() throws IOException {
        //Starts the webserver for WebService
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    /**
    * serves data to client, URI is catched and handled
    * @param session session of client, provided by client
    * @return Response with json data
    */
    @Override
    public Response serve(IHTTPSession session) {
        if(session.getUri().equals("/favicon.ico")){
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK,"image/x-icon", "");
        }
        LOG.info("WebService: URI access: " + session.getUri());
        String response = new JSONObject().put("error", "unkown URL: use /api/v1/weather/cities or /api/v1/weather/{cityID}").toString();

        String uri = session.getUri().toLowerCase();
        if (uri.startsWith("/api/v1/weather/")) {
            String uriModified = uri.replace("/api/v1/weather", ""); // remove unused part
            
            if (uriModified.equals("/cities")) {
                // When URL is: api/v1/weather/cities
                response = this.getAllCities();
                LOG.info("WebService: serving List of all Cities");
            } else {
                // When URL is: api/v1/weather/
                
                uriModified = uriModified.substring(1); // Remove / from string

                try {
                    int cityID = Integer.parseInt(uriModified);
                    response = this.getWeatherDataSingleDay(cityID);
                    if(response.equals("[]")){
                        // no data received from DB for this city
                        response = new JSONObject().put("error", "no data for this cityID").toString();
                    }
                    LOG.info("WebService: serving weatherdata for cityID: " + cityID);
                } catch (NumberFormatException ex) {
                    // when string contains more than numbers
                    response = new JSONObject().put("error", "invalid cityID").toString();
                    LOG.info("WebService: received invalid cityID");
                }
            }
        }
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/json", response);
    }

    /**
    * Gets all cities from DB as string
    * @return String with all cities
    */
    public String getAllCities() {
        WeatherDataProviderImpl AllCities = new WeatherDataProviderImpl();
        return AllCities.getAllCities().toString();
    }

    /**
    * Gets weatherdata for specific city as string
    * @param cityID 
    * @return String with weatherdata of last 24h of a city
    */
    public String getWeatherDataSingleDay(int cityID) {
        WeatherDataProviderImpl A02 = new WeatherDataProviderImpl();

        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DATE, -1);
        
        Calendar calendarEnd = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String endDatetime = formatter.format(calendarEnd.getTime());
        String startDatetime = formatter.format(calendarStart.getTime());

        // Format queryArray just how WeatherDataProvider expects it
        JSONArray queryArray = new JSONArray();
        JSONObject queryObject = new JSONObject();
        queryObject.put("startDatetime", startDatetime);
        queryObject.put("endDatetime", endDatetime);
        queryObject.put("abfrageID", 2);
        queryObject.put("cityID", cityID);
        queryArray.put(queryObject);

        return A02.getA02(queryArray).toString();
    }
}
