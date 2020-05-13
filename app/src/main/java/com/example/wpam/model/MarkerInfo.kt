package com.example.wpam.model

data class MarkerInfo(val Name: String,
                    val Description: String,
                    val miniaturePath: String?,
                    val positionX: String,
                    val positionY: String) {
    constructor() : this("", "", "", "","")
}