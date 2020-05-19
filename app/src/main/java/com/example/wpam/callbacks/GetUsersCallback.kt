package com.example.wpam.callbacks

import com.example.wpam.model.UserData

interface GetUsersCallback {
    fun onCallback(list: MutableList<UserData>)
}