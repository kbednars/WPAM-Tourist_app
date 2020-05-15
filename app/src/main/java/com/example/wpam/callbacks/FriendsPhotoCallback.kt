package com.example.wpam.callbacks

import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData

interface FriendsPhotoCallback {
    fun onCallback(map: MutableList<Pair<UserData?, PlacePhoto>>)
}