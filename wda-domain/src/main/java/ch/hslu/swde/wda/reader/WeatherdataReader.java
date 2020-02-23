package ch.hslu.swde.wda.reader;

import ch.hslu.swde.wda.database.DatabaseConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.math.*;

/**
 *
 * @author Lukas
 */
public class WeatherdataReader {

    final static Logger LOG = LogManager.getLogger(WeatherdataReader.class);

    /**
     * Gets all cities from the data provider via an url
     * @return JSONArray with all cities from rest Provider
     */
    public JSONArray getAllCities() {
        String url = "http://swde.el.eee.intern:8080/weatherdata-rws-provider/rest/weatherdata/cities";
        return getJsonArrayFromUrl(url);
    }

    /**
     * Gets all weather data for a specific city from the data provider via an url
     * @param city The city for which data is searched
     * @return JSONArray with the weather data for the specific city
     */
    private JSONArray getAllWeatherdataForSpecificCity(String city) {
        String url = "http://swde.el.eee.intern:8080/weatherdata-rws-provider/rest/weatherdata/" + city;
        return getJsonArrayFromUrl(url);
    }

    /**
     * Builds a JSONArray from the string read from the provided url
     * @param url to the data provider
     * @return JSONArray for the requested url data
     */
    private JSONArray getJsonArrayFromUrl(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL providerUrl = new URL(url); // URL to Parse
            URLConnection connection = providerUrl.openConnection();
            BufferedReader in = new BufferedReader( new InputStreamReader(connection.getInputStream(),"UTF8"));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine);
            in.close();
        } catch (FileNotFoundException e) {
            LOG.error("FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            LOG.error("IOException: Can't access " + e.getMessage());
        } catch (NullPointerException e){
            LOG.error("NullPointerException: " + e.getLocalizedMessage());
        }

        return new JSONArray(stringBuilder.toString());
    }

    /**
     *  Inserts all cities from the rest provider into the city table
     *  of the sqlite database, ignoring existing entries
     */
    public void insertCitiesIntoDB() {
        String sql = "INSERT OR IGNORE INTO city(cityID,name) VALUES(?,?)";
        JSONArray cityArray = getAllCities();

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
        } else {
            try {
                conn.setAutoCommit(false);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (Object o : cityArray) {
                    JSONObject city = (JSONObject) o;

                    pstmt.setInt(1, city.getInt("id"));
                    pstmt.setString(2, city.getString("name"));
                    pstmt.addBatch();
                }

                // execute the batch
                LOG.info("City insert/update");
                logDbInserts(checkUpdateCounts(pstmt.executeBatch()));

                // commit and close connection
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            } catch (NullPointerException e) {
                LOG.error(e);
            }
        }
    }

    /**
     * Inserts the weather data for a specific city into the weatherdata table
     * of the sqlite database,
     * ignoring existing entries
     * @param city for which data will be inserted into the database
     */
    public void insertWeatherDataIntoDB(String city) {
        String sql = "INSERT OR IGNORE INTO weatherdata"
                + "(weatherdataID, FK_cityID, update_time, weather_summary, weather_description, "
                + "temperature_celcius, pressure, humidity, wind_speed, wind_direction) VALUES(?,?,?,?,?,?,?,?,?,?)";
        JSONArray cityDataArray = getAllWeatherdataForSpecificCity(city);

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
        } else {
            try {
                conn.setAutoCommit(false);
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (Object o : cityDataArray) {
                    JSONObject weatherdata = (JSONObject) o;
                    //splits the Json Object "data" from the provider into a string array
                    String[] data = weatherdata.getString("data").split("#");
                    //splits the string array into correct a key-value relation
                    Map<String, String> weatherDetails = weatherDataParser(data);
                    //splits the update time from the provider to create a timestamp object for the database
                    String[] updateTime = weatherDetails.get("LAST_UPDATE_TIME").split("-|T|:");

                    pstmt.setInt(1, weatherdata.getInt("id"));
                    pstmt.setInt(2, weatherdata.getJSONObject("city").getInt("id"));
                    pstmt.setObject(3, updateTimeParser(updateTime).toString());
                    pstmt.setString(4, weatherDetails.get("WEATHER_SUMMARY"));
                    pstmt.setString(5, weatherDetails.get("WEATHER_DESCRIPTION"));
                    pstmt.setObject(6, parseToFloat(weatherDetails.get("CURRENT_TEMPERATURE_CELSIUS")));
                    pstmt.setObject(7, parseToFloat(weatherDetails.get("PRESSURE")));
                    pstmt.setObject(8, parseToFloat(weatherDetails.get("HUMIDITY")));
                    pstmt.setObject(9, parseToFloat(weatherDetails.get("WIND_SPEED")));
                    pstmt.setObject(10, parseToFloat(weatherDetails.get("WIND_DIRECTION")));
                    pstmt.addBatch();
                }

                // check if batches are valid or failing
                LOG.info("Weatherdata for city: " + city);
                logDbInserts(checkUpdateCounts(pstmt.executeBatch()));

                // commit and close connection
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
    }

    /**
     * writes logs depending on the success of the insert statements
     * @param updateCounter result from the batch execution
     */
    private void logDbInserts(int[] updateCounter) {
        if (updateCounter[0] == 0 && updateCounter[1] == 0 && updateCounter[2] == 0) {
            LOG.info("Statements were successful but no rows affected");
        } else {
            if (updateCounter[0] > 0) {
                LOG.info("rows inserted/updated: " + updateCounter[0]);
            }
            if (updateCounter[1] > 0) {
                LOG.info("statements successful but no row info: " + updateCounter[1]);
            }
            if (updateCounter[1] > 0) {
                LOG.error("statement failed: " + updateCounter[2]);
            }
        }
    }

    /**
     * 0 or greater — the command was processed successfully and the value is an update count
     * indicating the number of rows in the database that were affected by the command’s execution
     *
     * Statement.SUCCESS_NO_INFO — the command was processed successfully,
     * but the number of rows affected is unknown
     * @param updateCounts Int array which was returned from executeBatch()
     */
    private int[] checkUpdateCounts(int[] updateCounts) {
        int[] counter = new int[3];
        for (int i = 0; i < updateCounts.length; i++) {
            if (updateCounts[i] >= 0) {
                counter[0] += updateCounts[i];
            } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
                counter[1]++;
            } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
                counter[2]++;
            }
        }
        return counter;
    }

    /**
     * Parses a part of the weather data from the data provider into a format
     * that makes it easier to process in the database insert
     * @param dataToParse from the provider, part of the JSONArray
     * @return normalized weather data
     */
    private Map<String, String> weatherDataParser(String[] dataToParse) {
        Map<String, String> weatherDetails = new HashMap<>();
        for (int i=0; i < dataToParse.length; i++){
            String[] details = dataToParse[i].split(":");
            // the "update time" is seperated with ':' and can be in two formats:
            // "Hours:Minutes" or "Hours:Minutes:Seconds"
            if (details.length == 2){
                weatherDetails.put(details[0], details[1]);
            }else if (details.length == 3){
                weatherDetails.put(details[0], details[1] + ":" + details[2]);
            }else if (details.length == 4){
                weatherDetails.put(details[0], details[1] + ":" + details[2] + ":" + details[3]);
            }
        }
        return weatherDetails;
    }

    /**
     * Parses the update time from a string array into a DateTime,
     * used to prepare the data for the import into the database
     * @param updateTimeArray String array which includes: year, month, day, hour, minute
     *                        and maybe seconds
     * @return DateTime object
     */
    private DateTime updateTimeParser(String[] updateTimeArray) {
        int[] dateTimeArray = new int[6];
        DateTime updateTime;
        try {
            for (int j=0; j < updateTimeArray.length; j++) {
                dateTimeArray[j] = Integer.parseInt(updateTimeArray[j]);
            }
            updateTime = new DateTime(dateTimeArray[0]
                    , dateTimeArray[1]
                    , dateTimeArray[2]
                    , dateTimeArray[3]
                    , dateTimeArray[4]
                    , dateTimeArray[5]
                    , 0);
        }catch (NumberFormatException e) {
            return null;
        }
        return updateTime;
    }

    /**
     * Parses a string into a float value if possible, otherwise returns null.
     * Because certain decimal floating point values cannot be represented the same in binary,
     * some values will have more floating point values in the Database.
     * @param data String to parse
     * @return float value or null if string is "unknown"
     */
    private Float parseToFloat(String data){
        BigDecimal result = null;
        try {
            result = new BigDecimal(data);
            Float floatValue = result.setScale(4,RoundingMode.HALF_UP).floatValue();
            return floatValue;
        } catch (NumberFormatException e) {
            return null;
        }catch (NullPointerException e) {
            return null;
        }
    }
}