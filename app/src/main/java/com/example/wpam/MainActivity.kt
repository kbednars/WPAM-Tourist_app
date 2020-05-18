@file:Suppress("Annotator")

package com.example.wpam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.loginUtility.FacebookLogin
import com.example.wpam.loginUtility.GoogleLogin
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_reset_password.view.*

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var callbackManager: CallbackManager
    val RC_SIGN_IN: Int = 1
    lateinit var googleLogin: GoogleLogin
    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null && firebaseUser.isEmailVerified || firebaseUser!= null && firebaseUser.providerData.get(1).providerId == "facebook.com") {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        // Configure Google and Facebook Sign In
        googleLogin = GoogleLogin(firebaseAuth,this, RC_SIGN_IN)
        FacebookLogin(firebaseAuth,this, callbackManager)

        val signInButton = findViewById<View>(R.id.signInButton) as Button
        signInButton.setOnClickListener {
            if (emailField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(emailField.text.toString(),passwordField.text.toString()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        FirestoreUtility.initCurrentUserDataIfFirstTime {
                            // Sign in success, update UI with the signed-in user's information
                            if (firebaseAuth.currentUser!!.isEmailVerified) {
                                Log.d(TAG_emailLogin, "signInWithEmail:success")
                                val intent = Intent(this, HomePage::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.w(TAG_emailLogin, "signInWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext,
                                    "Check your E-mail for verification link",
                                    Toast.LENGTH_SHORT
                                ).show()
                                passwordField.text.clear()
                                firebaseAuth.signOut()
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG_emailLogin, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: Wrong E-mail or Password", Toast.LENGTH_SHORT).show()
                        passwordField.text.clear()
                    }
                }
            } else {
                Toast.makeText(baseContext, "You must fill out the fields", Toast.LENGTH_LONG)
                    .show()
                emailField.text.clear()
                passwordField.text.clear()
            }
        }

        val registerActivityButton = findViewById<View>(R.id.registerActivityButton) as Button
        registerActivityButton.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val resetPasswordButton = findViewById<View>(R.id.resetPasswordDialogButton) as Button
        resetPasswordButton.setOnClickListener{
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val emailResetEmail = mDialogView.findViewById<EditText>(R.id.emailResetField)
            val mAlertDialog = mBuilder.show()
            mDialogView.sendResetPassButton.setOnClickListener{
                firebaseAuth.sendPasswordResetEmail(emailResetEmail.text.toString()).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "E-mail with password reset link sent to:${emailResetEmail.text}",
                            Toast.LENGTH_SHORT).show()
                        mAlertDialog.dismiss()
                    } else {
                        Toast.makeText(baseContext, "Couldn't send e-mail with password reset link",
                            Toast.LENGTH_SHORT).show()
                        emailResetEmail.text.clear()
                    }
                }
            }
            mDialogView.cancelResetButton.setOnClickListener{
                mAlertDialog.dismiss()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this.authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this.authStateListener)
    }

    fun loggedSuc(){
        startActivity(Intent(this@MainActivity, HomePage::class.java).apply {
            putExtra(EXTRA_MESSAGE, "Zalogowano")
        })
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleLogin.handleGoogleResult (task)
        }
    }



    companion object {
        const val TAG_emailLogin = "EmailLogin"
        const val TAG_google = "GoogleLogin"
        const val TAG_facebook = "FacebookLogin"
    }
}
