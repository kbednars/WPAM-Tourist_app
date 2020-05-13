package com.example.wpam.callbacks
import com.example.wpam.model.MarkerInfo

interface MarkerCallback {
    fun onCallback(list: MutableList<MarkerInfo>)
}