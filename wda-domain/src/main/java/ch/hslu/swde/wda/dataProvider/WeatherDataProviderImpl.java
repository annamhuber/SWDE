package ch.hslu.swde.wda.dataProvider;


import ch.hslu.swde.wda.database.DatabaseConnector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.*;

/**
 *
 * @author Cedric
 *
 */
public class WeatherDataProviderImpl implements WeatherDataProviderInterface {

    final static Logger LOG = LogManager.getLogger(WeatherDataProviderImpl.class);
    
    /**
    * Fetch all Cities from DB (A01)
    * @return JSONArray with Cities as JSONObject
    */
    @Override
    public JSONArray getAllCities() {
        // A01
        JSONArray queryData = new JSONArray();

        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT cityID, name FROM city";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("cityID", rs.getInt("cityID"));
                obj.put("name", rs.getString("name"));
                queryData.put(obj);
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }
        LOG.info("Fetched CitiesData from DB");
        return queryData;
    }

    /**
    * Fetches all Data for A02 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contains startDatetime, endDatetime, abfrageID, cityID. Only first JSONObject is read.
    * @return JSONArray with all weatherData for one specific city and specific timeframe. Normally timeframe 24h, but it can be overwritten
    */
    @Override
    public JSONArray getA02(JSONArray abfrageArray) {
        // abfrageArray[i]: startDatetime, abfrageID, cityID, endDatetime
        JSONObject currentObject = abfrageArray.getJSONObject(0); //Get first object from Array. There is only one object in A02
        String startDatetime = currentObject.getString("startDatetime");
        String endDatetime = currentObject.getString("endDatetime");
        int cityID = currentObject.getInt("cityID");
        LOG.info("Fetching A02 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

        JSONArray queryData = new JSONArray();
        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT weatherdataID, update_time,weather_summary, weather_description, temperature_celcius, pressure, humidity, wind_speed, wind_direction "
                + "FROM weatherdata "
                + "WHERE FK_cityID = ? "
                + "AND update_time BETWEEN ? AND ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cityID);
            pstmt.setString(2, startDatetime);
            pstmt.setString(3, endDatetime);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject currentDataObject = new JSONObject();
                currentDataObject.put("weatherdataID", rs.getInt("weatherdataID"));
                currentDataObject.put("update_time", rs.getString("update_time"));
                currentDataObject.put("weather_summary", rs.getString("weather_summary"));
                currentDataObject.put("weather_description", rs.getString("weather_description"));
                currentDataObject.put("temperature_celcius", rs.getFloat("temperature_celcius"));
                currentDataObject.put("pressure", rs.getFloat("pressure"));
                currentDataObject.put("humidity", rs.getFloat("humidity"));
                currentDataObject.put("wind_speed", rs.getFloat("wind_speed"));
                currentDataObject.put("wind_direction", rs.getFloat("wind_direction"));
                queryData.put(currentDataObject); // Put JSONObject into Array
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }
        LOG.info("Fetched Data for A02. ");
        return queryData;
    }

    /**
    * Fetches all Data for A03 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contains startDatetime, endDatetime, abfrageID, cityID. Only first JSONObject is read.
    * @return JSONArray with JSONObjects which contain weatherdataID, update_time, temperature_celcius, pressure, humidity for one specific City and specific timeframe
    */
    @Override
    public JSONArray getA03(JSONArray abfrageArray) {
        JSONObject currentObject = abfrageArray.getJSONObject(0); //Get first object from Array. There is only one object in A03
        String startDatetime = currentObject.getString("startDatetime");
        String endDatetime = currentObject.getString("endDatetime");
        int cityID = currentObject.getInt("cityID");
        LOG.info("Fetching A03 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

        JSONArray queryData = new JSONArray();
        Connection conn = new DatabaseConnector().connect();
        if (conn == null) {
            LOG.error("No DB Connection");
            return queryData; //Empty
        }

        String sql = "SELECT weatherdataID, update_time, temperature_celcius, pressure, humidity "
                + "FROM weatherdata "
                + "WHERE FK_cityID = ? "
                + "AND update_time BETWEEN ? AND ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cityID);
            pstmt.setString(2, startDatetime);
            pstmt.setString(3, endDatetime);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject currentDataObject = new JSONObject();
                currentDataObject.put("weatherdataID", rs.getInt("weatherdataID"));
                currentDataObject.put("update_time", rs.getString("update_time"));
                currentDataObject.put("temperature_celcius", rs.getFloat("temperature_celcius"));
                currentDataObject.put("pressure", rs.getFloat("pressure"));
                currentDataObject.put("humidity", rs.getFloat("humidity"));
                queryData.put(currentDataObject); // Put JSONObject into Array
            }
            conn.close();
        } catch (SQLException e) {
            LOG.error("SQLLite Exception: " + e.getMessage());
        }
        LOG.info("Fetched Data for A03. ");
        return queryData;
    }

    /**
    * Fetches all Data for A04 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), average_temperature, startDatetime, endDatetime
    */
    @Override
    public JSONArray getA04(JSONArray abfrageArray) {
        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A04 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT cityID, name, temperature_celcius FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                int countRs = 0;
                float temperatureTotal = 0;
                String cityName = "";
                while (rs.next()) {
                    countRs++;
                    float currentTemperature = rs.getFloat("temperature_celcius");
                    temperatureTotal = temperatureTotal + currentTemperature;
                    cityName = rs.getString("name");
                }
                Float average_temperature = null;
                if (countRs > 0) {
                    average_temperature = temperatureTotal / (float) countRs; // Calculate average temperature
                }

                //Add to RETURN JSONARRAY 
                if (average_temperature != null) { // Only add when there are results
                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("cityID", cityID);
                    currentDataObject.put("name", cityName);
                    currentDataObject.put("average_temperature", average_temperature);
                    currentDataObject.put("startDatetime", startDatetime);
                    currentDataObject.put("endDatetime", endDatetime);
                    queryData.put(currentDataObject); // Put JSONObject into Array
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A04. ");
        return queryData;
    }

    /**
    * Fetches all Data for A05 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), min_temperature, max_temperature, startDatetime, endDatetime
    */
    @Override
    public JSONArray getA05(JSONArray abfrageArray) {
        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A05 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT cityID, name, max(temperature_celcius) AS max_temperature, min(temperature_celcius) AS min_temperature FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                cityID = rs.getInt("cityID");
                if (cityID != 0) {
                    // 0 means NULL -> no data
                    String cityName = rs.getString("name");
                    float min_temperature = rs.getFloat("min_temperature");
                    float max_temperature = rs.getFloat("max_temperature");

                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("cityID", cityID);
                    currentDataObject.put("name", cityName);
                    currentDataObject.put("min_temperature", min_temperature);
                    currentDataObject.put("max_temperature", max_temperature);
                    currentDataObject.put("startDatetime", startDatetime);
                    currentDataObject.put("endDatetime", endDatetime);
                    queryData.put(currentDataObject); // Put JSONObject into Array
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A05. " );
        return queryData;
    }

    /**
    * Fetches all Data for A06 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), average_temperature, startDatetime, endDatetime
    */
    @Override
    public JSONArray getA06(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A06 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT cityID, name, pressure FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                int countRs = 0;
                float pressureTotal = 0;
                String cityName = "";
                while (rs.next()) {
                    countRs++;
                    float currentPressure = rs.getFloat("pressure");
                    pressureTotal = pressureTotal + currentPressure;
                    cityName = rs.getString("name");
                }
                Float average_pressure = null;
                if (countRs > 0) {
                    average_pressure = pressureTotal / (float) countRs; // Calculate average temperature
                }

                //Add to RETURN JSONARRAY 
                if (average_pressure != null) { // Only add when there are results
                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("cityID", cityID);
                    currentDataObject.put("name", cityName);
                    currentDataObject.put("average_pressure", average_pressure);
                    currentDataObject.put("startDatetime", startDatetime);
                    currentDataObject.put("endDatetime", endDatetime);
                    queryData.put(currentDataObject); // Put JSONObject into Array
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A06. ");
        return queryData;
    }

    /**
    * Fetches all Data for A07 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), min_pressure, max_pressure, startDatetime, endDatetime
    */
    @Override
    public JSONArray getA07(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A07 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT cityID, name, max(pressure) AS max_pressure, min(pressure) AS min_pressure FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                cityID = rs.getInt("cityID");
                if (cityID != 0) {
                    // 0 means NULL -> no data
                    String cityName = rs.getString("name");
                    float min_pressure = rs.getFloat("min_pressure");
                    float max_pressure = rs.getFloat("max_pressure");

                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("cityID", cityID);
                    currentDataObject.put("name", cityName);
                    currentDataObject.put("min_pressure", min_pressure);
                    currentDataObject.put("max_pressure", max_pressure);
                    currentDataObject.put("startDatetime", startDatetime);
                    currentDataObject.put("endDatetime", endDatetime);
                    queryData.put(currentDataObject); // Put JSONObject into Array
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A07. ");
        return queryData;
    }
    
    /**
    * Fetches all Data for A08 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), average_humidity, startDatetime, endDatetime
    */
    @Override
    public JSONArray getA08(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A08 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT cityID, name, humidity FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                int countRs = 0;
                float humidityTotal = 0;
                String cityName = "";
                while (rs.next()) {
                    countRs++;
                    float currentHumidity = rs.getFloat("humidity");
                    humidityTotal = humidityTotal + currentHumidity;
                    cityName = rs.getString("name");
                }
                Float average_humidity = null;
                if (countRs > 0) {
                    average_humidity = humidityTotal / (float) countRs; // Calculate average temperature
                }

                //Add to RETURN JSONARRAY 
                if (average_humidity != null) { // Only add when there are results
                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("cityID", cityID);
                    currentDataObject.put("name", cityName);
                    currentDataObject.put("average_humidity", average_humidity);
                    currentDataObject.put("startDatetime", startDatetime);
                    currentDataObject.put("endDatetime", endDatetime);
                    queryData.put(currentDataObject); // Put JSONObject into Array
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A08. ");
        return queryData;
    }

    
    /**
    * Fetches all Data for A09 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), min_humidity, max_humidity, startDatetime, endDatetime
    */
    @Override
    public JSONArray getA09(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A09 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT cityID, name, max(humidity) AS max_humidity, min(humidity) AS min_humidity FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                cityID = rs.getInt("cityID");
                if (cityID != 0) {
                    // 0 means NULL -> no data
                    String cityName = rs.getString("name");
                    float min_humidity = rs.getFloat("min_humidity");
                    float max_humidity = rs.getFloat("max_humidity");

                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("cityID", cityID);
                    currentDataObject.put("name", cityName);
                    currentDataObject.put("min_humidity", min_humidity);
                    currentDataObject.put("max_humidity", max_humidity);
                    currentDataObject.put("startDatetime", startDatetime);
                    currentDataObject.put("endDatetime", endDatetime);
                    queryData.put(currentDataObject); // Put JSONObject into Array
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A09. ");
        return queryData;
    }

    /**
    * Fetches all Data for A10 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), weatherdataID, update_time, temperature_celcius
    */
    @Override
    public JSONArray getA10(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A10 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            JSONArray oneCityData = new JSONArray();
            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT weatherdataID, cityID, name, update_time, temperature_celcius  FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                boolean noData = true;
                while (rs.next()) {
                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("weatherdataID", rs.getInt("weatherdataID"));
                    currentDataObject.put("update_time", rs.getString("update_time"));
                    currentDataObject.put("name", rs.getString("name"));
                    currentDataObject.put("cityID", rs.getInt("cityID"));
                    currentDataObject.put("temperature_celcius", rs.getFloat("temperature_celcius"));
                    if (currentDataObject.length() != 0) {
                        // Only Add when Data
                        noData = false;
                        oneCityData.put(currentDataObject); // Put JSONObject into Array
                    }
                }
                if (cityID != 0 && noData == false) {
                    // 0 means NULL -> no data. dont put another empty array in empty array
                    queryData.put(oneCityData);
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A10. ");
        return queryData;
    }

    /**
    * Fetches all Data for A11 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), weatherdataID, update_time, humidity
    */
    @Override
    public JSONArray getA11(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A11 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            JSONArray oneCityData = new JSONArray();
            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT weatherdataID, cityID, name, update_time, humidity FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                boolean noData = true;
                while (rs.next()) {
                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("weatherdataID", rs.getInt("weatherdataID"));
                    currentDataObject.put("update_time", rs.getString("update_time"));
                    currentDataObject.put("name", rs.getString("name"));
                    currentDataObject.put("cityID", rs.getInt("cityID"));
                    currentDataObject.put("humidity", rs.getFloat("humidity"));
                    if (currentDataObject.length() != 0) {
                        // Only Add when Data
                        noData = false;
                        oneCityData.put(currentDataObject); // Put JSONObject into Array
                    }
                }
                if (cityID != 0 && noData == false) {
                    // 0 means NULL -> no data. dont put another empty array in empty array
                    queryData.put(oneCityData);
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A11. ");
        return queryData;
    }

    /**
    * Fetches all Data for A12 from DB with specific params
    * @param abfrageArray is a JSONArray with JSONObjects which contain startDatetime, endDatetime, abfrageID, cityID
    * @return JSONArray with JSONObjects which contain cityID, name (name of city), weatherdataID, update_time, pressure
    */
    @Override
    public JSONArray getA12(JSONArray abfrageArray) {

        JSONArray queryData = new JSONArray();

        for (int i = 0; i < abfrageArray.length(); i++) {
            JSONObject currentObject = abfrageArray.getJSONObject(i);
            String startDatetime = currentObject.getString("startDatetime");
            String endDatetime = currentObject.getString("endDatetime");
            int cityID = currentObject.getInt("cityID");
            LOG.info("Fetching A12 Data. CityID: " + cityID + " Timeframe: " + startDatetime + " - " + endDatetime);

            JSONArray oneCityData = new JSONArray();
            Connection conn = new DatabaseConnector().connect();
            if (conn == null) {
                LOG.error("No DB Connection");
                return queryData; //Empty
            }
            String sql = "SELECT weatherdataID, cityID, name, update_time, pressure FROM weatherdata, city "
                    + "WHERE FK_cityID = cityID AND cityID = ? "
                    + "AND update_time BETWEEN ? AND ?";

            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, cityID);
                pstmt.setString(2, startDatetime);
                pstmt.setString(3, endDatetime);

                ResultSet rs = pstmt.executeQuery();

                boolean noData = true;
                while (rs.next()) {
                    JSONObject currentDataObject = new JSONObject();
                    currentDataObject.put("weatherdataID", rs.getInt("weatherdataID"));
                    currentDataObject.put("update_time", rs.getString("update_time"));
                    currentDataObject.put("name", rs.getString("name"));
                    currentDataObject.put("cityID", rs.getInt("cityID"));
                    currentDataObject.put("pressure", rs.getFloat("pressure"));
                    if (currentDataObject.length() != 0) {
                        // Only Add when Data
                        noData = false;
                        oneCityData.put(currentDataObject); // Put JSONObject into Array
                    }
                }
                if (cityID != 0 && noData == false) {
                    // 0 means NULL -> no data. dont put another empty array in empty array
                    queryData.put(oneCityData);
                }
                conn.close();
            } catch (SQLException e) {
                LOG.error("SQLLite Exception: " + e.getMessage());
            }
        }
        LOG.info("Fetched Data for A12. ");
        return queryData;
    }
}
