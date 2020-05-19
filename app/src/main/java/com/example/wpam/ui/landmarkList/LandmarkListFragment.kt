package com.example.wpam.ui.landmarkList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.adapters.MarkersRecycleAdapter
import com.example.wpam.callbacks.GetMarkersCallback
import com.example.wpam.locationUtility.LocationUtility
import com.example.wpam.model.MarkerInfo
import com.example.wpam.ui.home.HomeViewModel
import com.example.wpam.ui.points.GetPointsViewModel


class LandmarkListFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var markersAdapter: MarkersRecycleAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance() = LandmarkListFragment()
    }

    private lateinit var viewModel: LandmarkListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Log.i("MyTAG", "Jestem w on Create Search")
       val root =  inflater.inflate(R.layout.fragment_landmark_list, container, false)

        val bundle = this.arguments
        val txt = (bundle?.getString("notificationId"))

        markersAdapter = activity?.let { MarkersRecycleAdapter(it) }!!
        recyclerView= root.findViewById(R.id.markers_list_recycler_view)
        linLayoutManager = LinearLayoutManager(activity)
        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter = markersAdapter
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = linLayoutManager.itemCount
                val lastVisible = linLayoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItemCount == lastVisible + 1) {
                    recyclerView.removeOnScrollListener(scrollListener)
                    addDataSet(0, totalItemCount + 2)
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
        if(markersAdapter.getItemCount() == 0)
            addDataSet(0, 2)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LandmarkListViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onPause() {
        super.onPause()
        var viewModel2 = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!
        viewModel2.search_pause.value = true;
       // Log.i("MyTAG", "Jestem w pause Search")

    }

    override fun onResume() {
       // Log.i("MyTAG", "Jestem w resume Search")
        var viewModel2 = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!
        viewModel2.search_pause.value = false;
        super.onResume()
    }
    private fun addDataSet(begin : Int, end : Int) {
        Log.i("lista", "jeszcze se nie wszedlem")
        LocationUtility.getMarkers(object : GetMarkersCallback {
            override fun onCallback(list: MutableList<Pair<MarkerInfo, String>>) {
                Log.i("lista", "lala")
                Log.i("lista", list.toString())
                markersAdapter.submitList(list)
                markersAdapter.notifyDataSetChanged()
            }
        })
    }
}
