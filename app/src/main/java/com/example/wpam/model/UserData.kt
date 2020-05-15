package com.example.wpam.model

data class UserData(val name: String,
                val description: String,
                val profilePicturePath: String?,
                val friendsAccounts: MutableList<String>,
                val points: Int) {
    constructor() : this("", "", "", mutableListOf(), 0)
}