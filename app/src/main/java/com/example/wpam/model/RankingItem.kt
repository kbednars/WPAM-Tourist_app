package com.example.wpam.model

data class RankingItem(val username: String,
                       val points: String,
                       val image: String?,
                       val uid: String) {
    constructor() : this("", "", "", "")
}