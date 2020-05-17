package com.example.wpam.ui.ranking

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
import com.example.wpam.adapters.RankingRecyclerAdapter
import com.example.wpam.callbacks.GetUsersCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.RankingItem
import com.example.wpam.model.UserData

class RankingFragment : Fragment() {

    private lateinit var rankingViewModel: RankingViewModel

    private lateinit var rankingAdapter: RankingRecyclerAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rankingViewModel =
            activity?.let { ViewModelProviders.of(it).get(RankingViewModel::class.java) }!!
        val root = inflater.inflate(R.layout.fragment_ranking, container, false)
        recyclerView= root.findViewById(R.id.ranking_recycler_view)
        linLayoutManager = LinearLayoutManager(activity)

        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            rankingAdapter = rankingViewModel.rankingAdapter
            adapter = rankingAdapter
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                recyclerView.removeOnScrollListener(scrollListener)
                Log.i("MyTAG", "Se jestem w lisynerze")
                val totalItemCount = linLayoutManager.itemCount
                val lastVisible = linLayoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItemCount == lastVisible + 1) {
                    addDataSet(0, totalItemCount + 2)
                }

                recyclerView.addOnScrollListener(scrollListener)
            }


        }

        recyclerView.addOnScrollListener(scrollListener)
        if(rankingAdapter.getItemCount() == 0)
            addDataSet(0,2)

        return root
    }

    private fun addDataSet(begin : Int, end : Int){
        val data = ArrayList<RankingItem>()
        FirestoreUtility.getUsersRanking(begin, end, object: GetUsersCallback {
            override fun onCallback(list: MutableList<UserData>) {
                for(photos in list){
                    var rankingItem = RankingItem(photos.name, photos.points.toString(), photos.profilePicturePath,photos.uid)
                    data.add(rankingItem)
                }
                rankingAdapter.submitList(data)
                rankingAdapter.notifyDataSetChanged()

            }
        })
    }


    override fun onPause() {
        super.onPause()
        rankingViewModel.linearLayoutManager.value = linLayoutManager.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        linLayoutManager.onRestoreInstanceState(rankingViewModel.linearLayoutManager.value)
    }

}
