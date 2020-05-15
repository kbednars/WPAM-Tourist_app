package com.example.wpam.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.DataSource
import com.example.wpam.adapters.PostRecyclerAdapter
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var blogAdapter: PostRecyclerAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = activity?.let { ViewModelProviders.of(it).get(HomeViewModel::class.java) }!!
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView= root.findViewById(R.id.recycler_view)
        linLayoutManager = LinearLayoutManager(activity)

        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            blogAdapter = homeViewModel.blogAdapter
            adapter = blogAdapter
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = linLayoutManager.itemCount
                val lastVisible = linLayoutManager.findLastVisibleItemPosition()

                if (totalItemCount == lastVisible + 1) {
                    addDataSet2()
                    blogAdapter.notifyDataSetChanged()
                }
            }

        }

        recyclerView.addOnScrollListener(scrollListener)
        if(blogAdapter.getItemCount() == 0)
            addDataSet()
        return root
    }

    private fun addDataSet(){
        val data = DataSource.createDataSet()
        blogAdapter.addList(data)
    }

    private fun addDataSet2(){
        val data = DataSource.createDataSet()
        blogAdapter.addList(data.subList(2,5))
    }


    override fun onPause() {
        super.onPause()
        homeViewModel.linearLayoutManager.value = linLayoutManager.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        linLayoutManager.onRestoreInstanceState(homeViewModel.linearLayoutManager.value)
    }
}
