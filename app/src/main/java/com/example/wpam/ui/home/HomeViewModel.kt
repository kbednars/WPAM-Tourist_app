package com.example.wpam.ui.home

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wpam.DataSource
import com.example.wpam.PostRecyclerAdapter

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    public var linearLayoutManager = MutableLiveData<Parcelable?>().apply {
        value = null
    }

    val text: LiveData<String> = _text



    public var blogAdapter: PostRecyclerAdapter = PostRecyclerAdapter()



    public lateinit var scrollListener: RecyclerView.OnScrollListener



}