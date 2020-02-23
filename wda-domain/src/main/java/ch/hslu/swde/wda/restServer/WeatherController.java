package ch.hslu.swde.wda.restServer;
// Class not used anymore due to micronauts removal

/*
import ch.hslu.swde.wda.dataProvider.WeatherDataProviderImpl;

import io.micronaut.http.annotation.*;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.discovery.event.ServiceStartedEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import io.reactivex.Flowable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
*/

/**
 *
 * @author Anna
 */

/*
@Controller("/api/v1/Weather")
public class WeatherController extends RuntimeException {
    final static Logger LOG = LogManager.getLogger(WeatherController.class);

    @Get("/Cities")
    public String GetAllCities(){
        WeatherDataProviderImpl AllCities = new WeatherDataProviderImpl();
        return AllCities.getAllCities().toString();
    }

    @Get("/{cityID}")
    public String getDataCity(int cityID){

        WeatherDataProviderImpl A02 = new WeatherDataProviderImpl();

        JSONArray queryArray = new JSONArray();
        JSONObject queryObject = new JSONObject();

        Calendar calendar = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.add(Calendar.DATE, -1);
        String endDatetime = formatter.format(calendar.getTime());
        String startDatetime = formatter.format(cal.getTime());

        queryObject.put("startDatetime", startDatetime);
        queryObject.put("endDatetime", endDatetime);
        queryObject.put("abfrageID", 2);
        queryObject.put("cityID", cityID);
        queryArray.put(queryObject);
        //[{"startDatetime":"2019-05-13 16:38:00","abfrageID":"2","cityID":"1",[]"endDatetime":"2019-05-14 16:38:00"}]

        return A02.getA02(queryArray).toString();
    }
}
*/