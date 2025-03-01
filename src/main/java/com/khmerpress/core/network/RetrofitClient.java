package com.khmerpress.core.network;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

class RetrofitClient {

    private static Retrofit mInstance;

    static synchronized Retrofit getClient(String baseUrl) {
        if (null == mInstance) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(1, TimeUnit.MINUTES);
            httpClient.connectTimeout(1, TimeUnit.MINUTES);
            httpClient.addInterceptor(logging);
            httpClient.proxy(Proxy.NO_PROXY);
            OkHttpClient client = httpClient.build();
            mInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .build();
        }
        return mInstance;
    }
}
