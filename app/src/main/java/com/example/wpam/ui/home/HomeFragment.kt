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
import com.example.wpam.adapters.PostRecyclerAdapter
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.callbacks.FriendsPhotoCallback
import com.example.wpam.callbacks.GetUsersCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.BlogPost
import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData
import kotlinx.coroutines.*

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
                    addDataSet(0, totalItemCount + 1)
                    blogAdapter.notifyDataSetChanged()
                }
            }

        }

        recyclerView.addOnScrollListener(scrollListener)
        if(blogAdapter.getItemCount() == 0)
            addDataSet(0, 1)
        blogAdapter.notifyDataSetChanged()
        return root
    }

    private fun addDataSet(begin : Int, end : Int) {
        val data = ArrayList<BlogPost>()
        /*GlobalScope.async {
            FirestoreUtility.getFriendsPlacePhotoPaths(begin, end, object : FriendsPhotoCallback {
                override fun onCallback(list: MutableList<Pair<UserData?, PlacePhoto>>) {
                    Log.i("FriendsPhtos:", list.toString())
                    for(photos in list){
                        var blogPost = BlogPost(photos.second.name, photos.second.description, photos.second.placePhotoPath, photos.first!!.name)
                        data.add(blogPost)
                    }
                    blogAdapter.addList(data)
                }
            })
        }*/

       FirestoreUtility.getUsersRanking(object: GetUsersCallback {
            override fun onCallback(list: MutableList<UserData>) {
                //Log.i("FriendsPhtos:", list.toString())
                Log.i("FriendsPhtos:", list.size.toString())

                for(photos in list){
                    var blogPost = BlogPost(photos.name, photos.description, photos.profilePicturePath, photos.name)
                    data.add(blogPost)
                    Log.i("FriendsPhtos:", blogPost.toString())
                }
                blogAdapter.addList(data)
                blogAdapter.notifyDataSetChanged()
            }
        })
       blogAdapter.notifyDataSetChanged()
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
