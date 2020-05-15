package com.example.wpam.model

data class BlogPost(val title: String,
           val body: String,
           val image: String?,
           var username: String,
            var uid : String) {
    constructor() : this("", "", "",  "", "")
}