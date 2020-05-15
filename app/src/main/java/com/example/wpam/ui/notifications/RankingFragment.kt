package com.example.wpam.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.DataSource
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.adapters.PostRecyclerAdapter
import com.example.wpam.adapters.RankingRecyclerAdapter

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
                val totalItemCount = linLayoutManager.itemCount
                val lastVisible = linLayoutManager.findLastVisibleItemPosition()

                if (totalItemCount == lastVisible + 1) {
                    addDataSet()
                    rankingAdapter.notifyDataSetChanged()
                }
            }

        }

        recyclerView.addOnScrollListener(scrollListener)
        if(rankingAdapter.getItemCount() == 0)
            addDataSet()

        return root
    }

    private fun addDataSet(){
        val data = DataSource.createDataSetRanking()
        rankingAdapter.addList(data)
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
