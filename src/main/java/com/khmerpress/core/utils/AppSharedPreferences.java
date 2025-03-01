package com.khmerpress.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.khmerpress.core.network.Config;


/**
 * Created by Socheat Leng on 26/12/2016
 */
public class AppSharedPreferences {

    public static final String APP_SHARED_PREFS = Config.Companion.getPreferenceName();
    private static AppSharedPreferences mAppShareConstant;
    private SharedPreferences _sharedPrefs;
    private Editor _prefsEditor;
    private String TAG = AppSharedPreferences.this.getClass().getSimpleName();

    private AppSharedPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS,
                Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public synchronized static AppSharedPreferences getConstant(Context context) {
        if (null == mAppShareConstant) {
            mAppShareConstant = new AppSharedPreferences(context);
        }
        return mAppShareConstant;
    }

    /**
     * Function for saving string to app shared preferences
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        _prefsEditor.putString(key, value);
        _prefsEditor.commit();
    }

    /**
     * Function for getting string from app shared preferences
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return _sharedPrefs.getString(key, "");
    }

    /**
     * Function for saving int value to app shared preferences
     *
     * @param key
     * @param value
     */
    public void setInt(String key, int value) {
        _prefsEditor.putInt(key, value);
        _prefsEditor.commit();
    }

    /**
     * Function for getting int value from app shared preferences
     *
     * @param key
     * @return
     */
    public int getInt(String key) {
        return _sharedPrefs.getInt(key, 0);
    }

    /**
     * Function for saving boolean value to app shared preferences
     *
     * @param key
     * @param value
     */
    public void setBoolean(String key, boolean value) {
        _prefsEditor.putBoolean(key, value);
        _prefsEditor.commit();
    }

    /**
     * Function for getting boolean value from app shared preferences
     *
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        return _sharedPrefs.getBoolean(key, false);
    }

    /**
     * Function for saving boolean value to app shared preferences
     *
     * @param key
     * @param value
     */
    public void setFloat(String key, float value) {
        _prefsEditor.putFloat(key, value);
        _prefsEditor.commit();
    }

    /**
     * Function for getting boolean value from app shared preferences
     *
     * @param key
     * @return
     */
    public float getFloat(String key) {
        return _sharedPrefs.getFloat(key, 0.0f);
    }

    public void clear() {
        _prefsEditor.clear();
    }

}
