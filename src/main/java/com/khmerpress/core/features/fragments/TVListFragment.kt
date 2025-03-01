package com.khmerpress.core.features.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.khmerpress.core.base.BaseFragment
import com.khmerpress.core.features.adapters.TVListAdapter
import com.khmerpress.core.features.models.Menu
import com.khmerpress.core.features.models.Station
import com.khmerpress.core.network.DataManager
import today.khmerpress.core.databinding.FragmentTvBinding

class TVListFragment : BaseFragment() {

    private lateinit var binding: FragmentTvBinding
    private var stationList: MutableList<Station> = mutableListOf()
    private var manager: GridLayoutManager? = null
    private var adapter: TVListAdapter? = null
    private var menu: Menu? = null

    companion object {
        fun newInstance(menu: Menu): TVListFragment {
            val args = Bundle()
            args.putSerializable("DATA", menu)
            val fragment = TVListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = requireActivity()
        menu = arguments?.get("DATA") as Menu
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTvBinding.inflate(layoutInflater)
        getData(true)
        return binding.root
    }

    private fun getData(show: Boolean) {
        if (show) {
            binding.infoView.showLoading()
        }
        DataManager.getInstance(activity).getTVs(menu?.id ?: "") { status ->
            if (status) {
                displayData()
            } else {
                binding.infoView.showInfo("Empty!!!")
            }
        }
    }

    private fun displayData() {
        binding.infoView.hide()
        stationList = DataManager.getInstance(activity).tvList
        stationList.shuffle()
        if (stationList.size > 12) {
            stationList.add(
                12,
                Station(
                    DataManager.getInstance(activity).getRectangle(),
                    "",
                    "",
                    isAd = true,
                    isRec = true
                )
            )
        }
        if (stationList.size > 6) {
            stationList.add(
                6,
                Station(
                    DataManager.getInstance(activity).getBanner(),
                    "",
                    "",
                    isAd = true,
                    isRec = false
                )
            )
        }
        if (stationList.size > 3) {
            stationList.add(
                3,
                Station(
                    DataManager.getInstance(activity).getRectangle(),
                    "",
                    "",
                    isAd = true,
                    isRec = true
                )
            )
        }
        manager = GridLayoutManager(activity, 3)
        binding.recyclerView.setLayoutManager(manager)
        adapter = TVListAdapter(activity, stationList)
        manager?.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItemViewType(position) == 0) {
                    3
                } else {
                    1
                }
            }
        }
        binding.recyclerView.isNestedScrollingEnabled = false
        binding.recyclerView.setAdapter(adapter)
    }
}