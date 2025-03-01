package com.khmerpress.core.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoreService {
    @GET("wp-data/drawing/drawing.json")
    Call<ResponseBody> getAppSettings(@Query("date") String date);
}
