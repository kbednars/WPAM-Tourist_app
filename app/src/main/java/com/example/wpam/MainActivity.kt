@file:Suppress("Annotator")

package com.example.wpam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth?=null
    var callbackManager: CallbackManager?=null
    val RC_SIGN_IN: Int = 1
    lateinit var googleLogin: GoogleLogin
    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, DisplayLoggedActivity::class.java)
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
        googleLogin = GoogleLogin(firebaseAuth!!, this, RC_SIGN_IN)
        FacebookLogin(firebaseAuth!!, this, callbackManager!!)

        val signInButton = findViewById<View>(R.id.signInButton) as Button
        signInButton.setOnClickListener{
            firebaseAuth!!.signInWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = firebaseAuth!!.currentUser
                        val intent = Intent(this, DisplayLoggedActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed: Wrong E-mail or Password",
                            Toast.LENGTH_SHORT).show()
                        passwordField.text.clear()
                    }
                }
        }

        val registerActivityButton = findViewById<View>(R.id.registerActivityButton) as Button
        registerActivityButton.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    public override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(this.authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth!!.removeAuthStateListener(this.authStateListener)
    }

    fun loggedSuc(){
        startActivity(Intent(this@MainActivity, DisplayLoggedActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "Zalogowano")
        })
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            googleLogin.handleGoogleResult (task)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    companion object {
        const val TAG = "FacebookLogin"
    }
}
