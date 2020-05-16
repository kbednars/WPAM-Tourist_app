package com.example.wpam.callbacks

import com.example.wpam.model.UserData

interface GetUserByIdCallback {
    fun onCallback(userData: UserData)
}