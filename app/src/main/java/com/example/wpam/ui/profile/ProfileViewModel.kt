package com.example.wpam.ui.profile

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wpam.adapters.PostRecyclerAdapter
import com.example.wpam.adapters.ProfileLandmarkListAdapter

class ProfileViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text

    public var linearLayoutManager = MutableLiveData<Parcelable?>().apply {
        value = null
    }

    public var profileLandmarkListAdapter: ProfileLandmarkListAdapter =
        ProfileLandmarkListAdapter()
}
