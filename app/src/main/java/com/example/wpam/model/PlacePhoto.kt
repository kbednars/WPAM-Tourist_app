package com.example.wpam.model

import com.google.firebase.Timestamp
import java.util.*

data class PlacePhoto(val name: String,
                      val description: String,
                      val placePhotoPath: String,
                      val likes: MutableList<String>,
                      val timeStamp: Date) {
    constructor() : this("","","", mutableListOf(), Timestamp.now().toDate())
}