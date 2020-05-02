package com.example.wpam.model

data class UserData(val name: String,
                val description: String,
                val profilePicturePath: String?) {
    constructor() : this("", "", "")
}