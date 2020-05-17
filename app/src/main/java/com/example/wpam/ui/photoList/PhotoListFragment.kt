package com.example.wpam.ui.photoList

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.adapters.PostRecyclerAdapter
import com.example.wpam.callbacks.FriendsPhotoCallback
import com.example.wpam.callbacks.PhotoCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.BlogPost
import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData
import com.example.wpam.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth


class PhotoListFragment : Fragment() {
    private lateinit var blogAdapter: PostRecyclerAdapter
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_photo_list, container, false)
        recyclerView= root.findViewById(R.id.photo_list_recycler_view)
        linLayoutManager = LinearLayoutManager(activity)
        blogAdapter = PostRecyclerAdapter()

        val bundle = this.arguments
        val position = (bundle?.getString("notificationId") ?: "nobody")

        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter = blogAdapter
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = linLayoutManager.itemCount
                val lastVisible = linLayoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItemCount == lastVisible + 1) {
                    addDataSet(0, totalItemCount + 2)
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
        addDataSet(0, position.toInt() +2)
        recyclerView.scrollToPosition(position.toInt())

        Log.i("MyTAG", position)
        return root
    }

    private fun addDataSet(begin : Int, end : Int) {
        val data = ArrayList<BlogPost>()
        FirestoreUtility.getCurrentUserPhotoCollection(0,10,object: PhotoCallback {
            override fun onCallback(list: MutableList<PlacePhoto>) {
                for(photos in list){
                    var blogPost = BlogPost(photos.name, photos.description, photos.placePhotoPath, "me", photos.likes.size,
                        photos.likes.contains(FirebaseAuth.getInstance().currentUser?.uid.toString()) , "me" )
                    data.add(blogPost)
                }
                blogAdapter.submitList(data)
                blogAdapter.notifyDataSetChanged()

            }
        })
    }

}
