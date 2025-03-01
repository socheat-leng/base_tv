package com.khmerpress.core.network;

import android.app.Activity;
import android.content.Context;

import com.khmerpress.core.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataManager {

    private Context mContext;
    private String TAG = DataManager.this.getClass().getSimpleName();

    public DataManager(Context context) {
        mContext = context;
    }

    private String appSettings = "";

    private static DataManager mInstance;

    public static DataManager getInstance(Activity mActivity) {
        if (null == mInstance) {
            mInstance = new DataManager(mActivity);
        }
        return mInstance;
    }

    /**
     * Get Settings
     */
    public void getSettings(final OnResponseListener onResponseListener) {
        Callback<ResponseBody> callback = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    appSettings = str;
                    JSONObject object = new JSONObject(str);
                    JSONObject easy = object.getJSONObject("easy");
                    JSONArray small = easy.getJSONArray("small");
                    if (null != onResponseListener) {
                        onResponseListener.onResponded(true);
                    }
                } catch (Exception e) {
                    if (null != onResponseListener) {
                        onResponseListener.onResponded(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (null != onResponseListener) {
                    onResponseListener.onResponded(false);
                }
            }
        };
        long date = System.currentTimeMillis() / 1000;
        ApiHelper.getAPIService().getAppSettings(date + "").enqueue(callback);
    }

    public String getAppSettings() {
        return appSettings;
    }
}
