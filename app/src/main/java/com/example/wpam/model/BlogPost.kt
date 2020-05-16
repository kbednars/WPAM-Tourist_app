package com.example.wpam.model

data class BlogPost(val title: String,
           val body: String,
           val image: String?,
           var username: String,
            var likes : Int,
                    var isliked: Boolean,
            var uid : String) {
    constructor() : this("", "", "",  "", 0, false, "")
}