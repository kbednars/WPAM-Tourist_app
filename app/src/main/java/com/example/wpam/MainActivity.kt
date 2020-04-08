package com.example.wpam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class MainActivity : AppCompatActivity() {

    var firebaseAuth: FirebaseAuth?=null
    var callbackManager: CallbackManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Facebook Login button
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        buttonFacebookLogin.setReadPermissions("email", "public_profile")
        buttonFacebookLogin.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)

                startActivity(Intent(this@MainActivity, DisplayMessageActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "Zalogowano")
                })
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                // ...
            }
        })
    }

    private fun handleFacebookAccessToken(accesToken: AccessToken?){
        val credential = FacebookAuthProvider.getCredential(accesToken!!.token)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnFailureListener{ e->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener { result->
                val email = result.user.email
                Toast.makeText(this, "You logged with email: "+email, Toast.LENGTH_LONG).show()

            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)

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
        private const val TAG = "FacebookLogin"
    }
}
