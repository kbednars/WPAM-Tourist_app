package com.example.wpam.ui.friendList

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
import com.example.wpam.adapters.FriendRecyclerAdapter
import com.example.wpam.adapters.ProfileRecyclerAdapter
import com.example.wpam.callbacks.GetUsersCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.UserData


class FriendListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var friendListAdapter: FriendRecyclerAdapter


    private lateinit var viewModel: FriendListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_friend_list, container, false)
        recyclerView = root.findViewById<RecyclerView>(R.id.friend_list_recycler_view)
        linLayoutManager = LinearLayoutManager(activity)
        friendListAdapter = FriendRecyclerAdapter()
        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter =  friendListAdapter
        }
        addDataSet()
        return root
    }

    private fun addDataSet() {
        FirestoreUtility.getFriendsData(object: GetUsersCallback{
            override fun onCallback(list: MutableList<UserData>) {
                friendListAdapter.submitList(list)
                friendListAdapter.notifyDataSetChanged()
            }
        })

    }

}
