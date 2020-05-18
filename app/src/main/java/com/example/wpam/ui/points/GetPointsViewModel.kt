package com.example.wpam.ui.points

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wpam.model.MarkerInfo

class GetPointsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    public var dupa  = MutableLiveData<String>().apply {
        value = "dupa"
    }


    public var search  = MutableLiveData<Boolean>().apply{
        value = false
    }
    public var search_pause  = MutableLiveData<Boolean>().apply{
        value = false
    }

    public var marker: MarkerInfo? = null

    public lateinit var selectedImageBytes: ByteArray
    public  var pictureJustChanged = false
}
