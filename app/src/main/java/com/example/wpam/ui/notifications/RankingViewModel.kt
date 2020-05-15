package com.example.wpam.ui.notifications

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wpam.adapters.RankingRecyclerAdapter

class RankingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    public var rankingAdapter: RankingRecyclerAdapter =
        RankingRecyclerAdapter()

    public var linearLayoutManager = MutableLiveData<Parcelable?>().apply {
        value = null
    }

}