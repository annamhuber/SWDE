package ch.hslu.swde.wda.dataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Lukas
 */
class WeatherDataProviderImplTest {

    @Test
    void testGetAllCities() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray array = instance.getAllCities();
        assertTrue(array == new JSONArray() ||
                array.getJSONObject(0).getInt("cityID") != 0 &&
                array.getJSONObject(0).getString("name") != "");
    }

    @Test
    void testAbfrage02Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"2\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA02(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage02IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //Invalid cityID in dummy request
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"2\",\"cityID\":0,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA02(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage03Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"3\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA03(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage03IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //end time before start time
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-04-01T12:00:00.000+01:00\",\"abfrageID\":\"3\",\"cityID\":40,\"endDatetime\":\"2018-01-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA03(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage04Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"4\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA04(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage04ThrowsException() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //start time missing in dummy request
        JSONArray dummyRequest = new JSONArray("[{\"abfrageID\":\"4\",\"cityID\":0,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        Assertions.assertThrows(JSONException.class,() -> instance.getA04(dummyRequest));
    }

    @Test
    void testAbfrage05Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"5\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA05(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage05IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //end time before start time
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-04-01T12:00:00.000+01:00\",\"abfrageID\":\"5\",\"cityID\":40,\"endDatetime\":\"2018-01-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA05(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage06Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //missing start time value
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"\",\"abfrageID\":\"6\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA06(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage06IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //missing end time value
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"6\",\"cityID\":0,\"endDatetime\":\"\"}]");
        JSONArray data = instance.getA06(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage07Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"7\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA07(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage07IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //end time before start time
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-04-01T12:00:00.000+01:00\",\"abfrageID\":\"7\",\"cityID\":40,\"endDatetime\":\"2018-01-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA07(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage08Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //abfrageID value missing
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA08(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage08IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //Invalid cityID in dummy request
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"8\",\"cityID\":0,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA08(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage09Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"9\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA09(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage09IsEmpty() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //end time before start time
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-04-01T12:00:00.000+01:00\",\"abfrageID\":\"9\",\"cityID\":40,\"endDatetime\":\"2018-01-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA09(dummyRequest);
        assertTrue(data.isEmpty());
    }

    @Test
    void testAbfrage10Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"10\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA10(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage10ThrowsException() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //cityID missing in dummy request
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"10\",\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        Assertions.assertThrows(JSONException.class,() -> instance.getA10(dummyRequest));
    }

    @Test
    void testAbfrage11Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"11\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA11(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage11ThrowException() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //start time missing in dummy request
        JSONArray dummyRequest = new JSONArray("[{\"abfrageID\":\"11\",\"cityID\":40,\"endDatetime\":\"2018-01-01T132:00:00.000+01:00\"}]");
        Assertions.assertThrows(JSONException.class,() -> instance.getA11(dummyRequest));
    }

    @Test
    void testAbfrage12Success() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-01-01T12:00:00.000+01:00\",\"abfrageID\":\"12\",\"cityID\":39,\"endDatetime\":\"2019-05-01T132:00:00.000+01:00\"}]");
        JSONArray data = instance.getA12(dummyRequest);
        assertFalse(data.isEmpty());
    }

    @Test
    void testAbfrage12ThrowException() {
        WeatherDataProviderImpl instance = new WeatherDataProviderImpl();
        //end time missing in dummy request
        JSONArray dummyRequest = new JSONArray("[{\"startDatetime\":\"2019-04-01T12:00:00.000+01:00\",\"abfrageID\":\"12\",\"cityID\":40}]");
        Assertions.assertThrows(JSONException.class,() -> instance.getA12(dummyRequest));
    }
}