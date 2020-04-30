package com.example.wpam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class DisplayLoggedActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager
    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_logged)

        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.loggedText).apply {
            text = message
        }

        val signOutGoogleButton = findViewById<View>(R.id.google_sign_out_button) as Button
        signOutGoogleButton.setOnClickListener{
                view: View? -> firebaseAuth.signOut()
                loginManager.logOut()
                Toast.makeText(this, "You logged out", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "Zalogowano")})
            finish()
        }
    }

    public override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this.authStateListener)
    }

    public override fun onResume() {
        super.onResume()
        checkAuthState()
    }

    private fun checkAuthState(){
        Log.d(TAG, "chceckAuthState: checking authenticitation state")
        val user = firebaseAuth.currentUser
        if(user == null){
            Log.d(TAG, "chceckAuthState: user is null")
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }else{
            Log.d(TAG, "chceckAuthState: user is authenticated")
        }
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this.authStateListener)
    }

    companion object {
        const val TAG = "DisplayLoggedActivity"
    }
}
