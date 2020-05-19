package com.example.wpam.callbacks

import com.example.wpam.model.PlacePhoto

interface PhotoCallback {
    fun onCallback(list: MutableList<PlacePhoto>)
}