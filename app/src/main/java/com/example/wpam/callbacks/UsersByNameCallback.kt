package com.example.wpam.callbacks

import com.example.wpam.model.UserData

interface UsersByNameCallback {
    fun onCallback(list: MutableList<UserData>)
}