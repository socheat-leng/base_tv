package com.khmerpress.core.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface CoreService {

    @FormUrlEncoded
    @POST
    Call<ResponseBody> getAppSettings(@Url String url, @Field("authkey") String auth_key);

    @FormUrlEncoded
    @POST
    Call<ResponseBody> getTVs(
            @Url String url,
            @Field("accesskey") String access_key,
            @Field("id") String type
    );
}
