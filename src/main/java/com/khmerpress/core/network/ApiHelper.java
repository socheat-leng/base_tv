package com.khmerpress.core.network;


/**
 * Created by Socheat on 2/19/2017.
 */

public class ApiHelper {

    public static final String BASE_URL = "https://khmerpress.today/";

    public static CoreService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(CoreService.class);
    }
}
