package com.example.wpam.loginUtility

import android.util.Log
import android.widget.Toast
import com.example.wpam.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class FacebookLogin(
    var firebaseAuth: FirebaseAuth,
    var activity: MainActivity,
    callbackManager: CallbackManager
){
    init{
        // Initialize Facebook Login
        activity.buttonFacebookLogin.setReadPermissions("email", "public_profile")
        activity.buttonFacebookLogin.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(MainActivity.TAG_facebook, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
                activity.loggedSuc()
            }
            override fun onCancel() {
                Log.d(MainActivity.TAG_facebook, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(MainActivity.TAG_facebook, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun handleFacebookAccessToken(accesToken: AccessToken?){
        val credential = FacebookAuthProvider.getCredential(accesToken!!.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnFailureListener{ e->
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener { result->
                val email = result.user!!.email
                val uid = result.user!!.uid
                Log.d(MainActivity.TAG_facebook,"Facebook login with user:"+uid)
                Toast.makeText(activity, "You logged with email: "+email, Toast.LENGTH_LONG).show()
            }
    }
}