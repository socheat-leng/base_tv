package com.khmerpress.core.features.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.khmerpress.core.features.activities.ChannelActivity
import com.khmerpress.core.features.models.Station
import com.khmerpress.core.features.viewholders.AdViewHolder
import com.khmerpress.core.utils.Constants
import today.khmerpress.core.R
import today.khmerpress.core.databinding.ItemTvBinding

class TVListAdapter(
    private val mActivity: Activity,
    private val stationsList: List<Station>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(mActivity)
        return if (viewType == 1) {
            val view = inflater.inflate(R.layout.item_tv, parent, false)
            TVViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_ad, parent, false)
            AdViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val station = stationsList[position]
        when (holder) {
            is AdViewHolder -> {
                val adView = if (station.isRec) {
                    AdView(mActivity, station.id, AdSize.RECTANGLE_HEIGHT_250)
                } else {
                    AdView(mActivity, station.id, AdSize.BANNER_HEIGHT_50)
                }
                holder.binding.layout.addView(adView)
                adView.loadAd()
            }

            is TVViewHolder -> {
                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.ic_holder)
                    .error(R.drawable.ic_holder)
                Glide.with(holder.binding.imgStation)
                    .setDefaultRequestOptions(requestOptions)
                    .load(station.image)
                    .into(holder.binding.imgStation)

                holder.binding.txtStation.text = station.name
                holder.binding.layout.setOnClickListener {
                    val intent = Intent(mActivity, ChannelActivity::class.java).apply {
                        putExtra(Constants.STATION, station)
                    }
                    mActivity.startActivity(intent)
                    mActivity.overridePendingTransition(R.anim.enter, R.anim.hold)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (stationsList[position].isAd) 0 else 1
    }

    override fun getItemCount(): Int = stationsList.size
}

class TVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: ItemTvBinding = ItemTvBinding.bind(itemView)
}