package com.example.wpam.callbacks

import com.example.wpam.model.PlacePhoto

interface PlacesPhotoPathCallback {
    fun onCallback(list: MutableList<PlacePhoto>)
}