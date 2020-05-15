package com.example.wpam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class BootActivity : Activity() {
    lateinit var firebaseAuth: FirebaseAuth
    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified || firebaseUser != null && firebaseUser.providerData.get(1).providerId == "facebook.com") {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener(this.authStateListener)
    }
}