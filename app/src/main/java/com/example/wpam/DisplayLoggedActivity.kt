package com.example.wpam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class DisplayLoggedActivity : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth?=null
    var loginManager: LoginManager?=null

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
                view: View? -> firebaseAuth!!.signOut()
                loginManager!!.logOut()
                Toast.makeText(this, "You logged out", Toast.LENGTH_LONG).show()
                finish()
                startActivity(Intent(this@DisplayLoggedActivity, MainActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "Zalogowano")})
        }

    }
}
