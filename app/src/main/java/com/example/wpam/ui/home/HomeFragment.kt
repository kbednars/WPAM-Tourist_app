package com.example.wpam.ui.home

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.DataSource
import com.example.wpam.PostRecyclerAdapter
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var blogAdapter: PostRecyclerAdapter

    private var lastVisibleItemPosition: Int = LinearLayoutManager(activity).findLastVisibleItemPosition()

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    private lateinit var linLayoutManager: LinearLayoutManager



    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            activity?.let { ViewModelProviders.of(it).get(HomeViewModel::class.java) }!!
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
                Log.i("MyTAG", "STATE STATE")

                val totalItemCount = linLayoutManager!!.itemCount
                val lastVisible = linLayoutManager!!.findLastVisibleItemPosition()

                Log.i("MyTAG", totalItemCount.toString())
                Log.i("MyTAG", lastVisible.toString())
                if (totalItemCount == lastVisible + 1) {
                    addDataSet2()
                    blogAdapter.notifyDataSetChanged()
                    Log.i("MyTAG", "Load new list")
                }
            }

        }

        recyclerView.addOnScrollListener(scrollListener)

        lastVisibleItemPosition = linLayoutManager.findLastVisibleItemPosition()

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
        homeViewModel?.linearLayoutManager.value = linLayoutManager.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        linLayoutManager.onRestoreInstanceState(homeViewModel?.linearLayoutManager.value)
    }
}
