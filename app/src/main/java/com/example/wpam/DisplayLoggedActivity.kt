package com.example.wpam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this.authStateListener)
    }
}
