package com.example.wpam.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.adapters.ProfileLandmarkListAdapter
import com.example.wpam.adapters.ProfileRecyclerAdapter
import com.example.wpam.callbacks.GetUsersCallback
import com.example.wpam.callbacks.PhotoCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.BlogPost
import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData
import com.google.firebase.auth.FirebaseAuth

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var searchAdapter: ProfileRecyclerAdapter

    private lateinit var scrollListener: RecyclerView.OnScrollListener
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search, container, false)
        val searchBar = root.findViewById(R.id.search_bar) as SearchView
        recyclerView = root.findViewById<RecyclerView>(R.id.search_recycle_view)
        linLayoutManager = LinearLayoutManager(activity)
        searchAdapter = ProfileRecyclerAdapter()
        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter =  searchAdapter
        }

        searchBar.queryHint = "Find by username"
        searchBar.setOnQueryTextListener(object :  SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                addDataSet(query)
                return false
            }

        })




        return root
    }

    private fun addDataSet(query: String) {
        FirestoreUtility.getUsersByName(query, object: GetUsersCallback {
            override fun onCallback(list: MutableList<UserData>) {
               searchAdapter.submitList(list)
                searchAdapter.notifyDataSetChanged()
            }
        })
    }
}
