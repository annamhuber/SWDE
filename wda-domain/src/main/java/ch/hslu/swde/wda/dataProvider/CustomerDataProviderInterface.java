package ch.hslu.swde.wda.dataProvider;

import org.json.JSONArray;


public interface CustomerDataProviderInterface {

    JSONArray getCustomersDataRequest();

    public void addCustomer(JSONArray userData);

    public void editCustomer(JSONArray userData);

    public void deleteCustomer(JSONArray userData);
}
