package com.khmerpress.core.network

import android.app.Activity
import android.content.Context
import com.khmerpress.core.features.models.Menu
import com.khmerpress.core.features.models.Station
import com.khmerpress.core.interfaces.OnResponseListener
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

class DataManager private constructor(private val context: Context) {

    companion object {
        private var instance: DataManager? = null
        fun getInstance(activity: Activity): DataManager =
            instance ?: synchronized(this) {
                instance ?: DataManager(activity).also { instance = it }
            }
    }

    private val tag = this::class.java.simpleName
    private var appSettings: String = ""

    val interstitialAds = mutableListOf<String>()
    val bannerAds = mutableListOf<String>()
    val rectangleAds = mutableListOf<String>()
    val natives = mutableListOf<String>()
    val nativeBanners = mutableListOf<String>()
    var adFrequency: Int = 0
    var adStatus: String = "0"
    var version: Int = 0
    var review: Int = 0
    var tvList = mutableListOf<Station>()
    var menuList = mutableListOf<Menu>()
    fun getAppSettings(): String = appSettings

    fun getSettings(onResponseListener: OnResponseListener?) {
        interstitialAds.clear()
        bannerAds.clear()
        rectangleAds.clear()
        natives.clear()
        nativeBanners.clear()
        val callback = object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    response.body()?.string()?.let { str ->
                        appSettings = str
                        val jsonObject = JSONObject(str)
                        adFrequency = jsonObject.optInt("ad")
                        adStatus = jsonObject.optString("ad_status")
                        version = jsonObject.getInt("version")
                        review = jsonObject.getInt("review")
                        val dataArray = jsonObject.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)
                            val id = obj.getString("id")
                            val name = obj.getString("name")
                            val icon = obj.getString("logo")
                            menuList.add(Menu(id, name, icon))
                        }
                        val ads: JSONObject? = jsonObject.optJSONObject("ads")
                        if (null != ads) {
                            val banners = ads.optJSONArray("banners")
                            if (null != banners) {
                                for (i in 0 until banners.length()) {
                                    bannerAds.add(banners.getString(i))
                                }
                            }
                            val interstitials = ads.optJSONArray("interstitails")
                            if (null != interstitials) {
                                for (i in 0 until interstitials.length()) {
                                    interstitialAds.add(interstitials.getString(i))
                                }
                            }
                            val rectangles = ads.optJSONArray("rectangles")
                            if (null != rectangles) {
                                for (i in 0 until rectangles.length()) {
                                    rectangleAds.add(rectangles.getString(i))
                                }
                            }
                            val fullscreens = ads.optJSONArray("natives")
                            if (null != fullscreens) {
                                for (i in 0 until fullscreens.length()) {
                                    natives.add(fullscreens.getString(i))
                                }
                            }
                            val native_banners = ads.optJSONArray("native_banners")
                            if (null != native_banners) {
                                for (i in 0 until native_banners.length()) {
                                    nativeBanners.add(native_banners.getString(i))
                                }
                            }
                        }
                        onResponseListener?.onResponded(true)
                    } ?: onResponseListener?.onResponded(false)
                } catch (e: Exception) {
                    onResponseListener?.onResponded(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResponseListener?.onResponded(false)
            }
        }
        val url = ApiHelper.BASE_URL + Config.settings
        ApiHelper.getAPIService().getAppSettings(url, Config.auth_key).enqueue(callback)
    }

    fun getTVs(id: String, onResponseListener: OnResponseListener?) {
        tvList = mutableListOf()
        val callback = object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    response.body()?.string()?.let { str ->
                        val jsonObject = JSONObject(str)
                        val status: Int = jsonObject.getInt("status")
                        if (status == 1) {
                            val data: JSONArray = jsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val obj = data.getJSONObject(i)
                                val stations: Station? = parseStations(obj)
                                if (null != stations) {
                                    tvList.add(stations)
                                }
                            }
                            onResponseListener?.onResponded(true)
                        } else {
                            onResponseListener?.onResponded(false)
                        }
                    } ?: onResponseListener?.onResponded(false)
                } catch (e: Exception) {
                    onResponseListener?.onResponded(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResponseListener?.onResponded(false)
            }
        }
        val url = ApiHelper.BASE_URL + Config.tvs
        ApiHelper.getAPIService().getTVs(url, Config.auth_key, id).enqueue(callback)
    }

    private fun parseStations(obj: JSONObject): Station? {
        return try {
            val id = obj.getString("id")
            val name = obj.getString("title")
            val category = obj.optString("category")
            val subcategory = obj.optString("subcategory")
            val image = obj.getString("image")
            val type = obj.getString("type")
            val url = obj.getString(Config.url_key_value)
            val external = obj.getString("external")
            val content = obj.getString("content")
            Station(id, name, url, category, subcategory, image, type, external).apply {
                this.content = content
            }
        } catch (e: Exception) {
            null
        }
    }


    fun getInterstitial(): String {
        try {
            val rand = Random()
            return interstitialAds[rand.nextInt(interstitialAds.size)]
        } catch (e: java.lang.Exception) {
            return ""
        }
    }

    fun getBanner(): String {
        try {
            val rand = Random()
            return bannerAds[rand.nextInt(bannerAds.size)]
        } catch (e: java.lang.Exception) {
            return ""
        }
    }

    fun getRectangle(): String {
        try {
            val rand = Random()
            return rectangleAds[rand.nextInt(rectangleAds.size)]
        } catch (e: java.lang.Exception) {
            return ""
        }
    }

    fun getNative(): String {
        try {
            val rand = Random()
            return natives[rand.nextInt(natives.size)]
        } catch (e: java.lang.Exception) {
            return ""
        }
    }

    fun getNativeBanners(): String {
        try {
            val rand = Random()
            return nativeBanners[rand.nextInt(nativeBanners.size)]
        } catch (e: java.lang.Exception) {
            return ""
        }
    }

}
