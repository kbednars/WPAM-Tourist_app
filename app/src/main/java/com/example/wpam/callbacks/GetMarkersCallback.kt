package com.example.wpam.callbacks

import com.example.wpam.model.MarkerInfo

interface GetMarkersCallback {
    fun onCallback(list: MutableList<Pair<MarkerInfo, String>>)
}