package com.example.wpam.loginUtility

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.wpam.MainActivity
import com.example.wpam.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleLogin(
    var firebaseAuth: FirebaseAuth,
    var activity: MainActivity,
    val RC_SIGN_IN: Int
){
    lateinit var mGoogleSignInClient: GoogleSignInClient

    init{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(activity,gso)
        val signInGoogleButton = activity.findViewById<View>(R.id.sign_in_button) as SignInButton
        signInGoogleButton.setOnClickListener { view: View? ->
            signInGoogle()
        }
    }

    private fun signInGoogle(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleGoogleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account!!)
        }catch (e: ApiException){
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(MainActivity.TAG_google, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(MainActivity.TAG_google, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    Toast.makeText(activity, "You logged with email: "+user!!.email, Toast.LENGTH_LONG).show()
                    activity.loggedSuc()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(MainActivity.TAG_google, "signInWithCredential:failure", task.exception)
                    Toast.makeText(activity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}