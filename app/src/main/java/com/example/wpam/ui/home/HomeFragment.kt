package com.example.wpam.ui.home

import android.os.Bundle
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

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var blogAdapter: PostRecyclerAdapter

    private var lastVisibleItemPosition: Int = LinearLayoutManager(activity).findLastVisibleItemPosition()

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView: RecyclerView = root.findViewById(R.id.recycler_view)
        recyclerView.apply{
            layoutManager = LinearLayoutManager(activity)
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            blogAdapter = PostRecyclerAdapter()
            adapter = blogAdapter
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.i("MyTAG", "STATE STATE")

                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager!!.findLastVisibleItemPosition()

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

        lastVisibleItemPosition = LinearLayoutManager(activity).findLastVisibleItemPosition()

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

     private fun initRecyclerView(){
         recycler_view.apply{
             layoutManager = LinearLayoutManager(activity)
             blogAdapter = PostRecyclerAdapter()
             adapter = blogAdapter
         }
     }



}
