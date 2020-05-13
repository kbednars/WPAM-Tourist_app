package com.example.wpam.model

data class UserData(val name: String,
                val description: String,
                val profilePicturePath: String?,
                val visitedPlaces: MutableList<String>,
                val placesPhotoPaths: MutableList<String>,
                val friendsAccounts: MutableList<String>) {
    constructor() : this("", "", "", mutableListOf(), mutableListOf(), mutableListOf())
}