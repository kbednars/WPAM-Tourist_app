package com.example.wpam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        val registerButton = findViewById<View>(R.id.registerButton) as Button
        registerButton.setOnClickListener{
            if(!emailFieldRegistration.text.isNullOrEmpty() && !passwordFieldRegistration.text.isNullOrEmpty() &&
                    !passwordConfirmationFieldRegistration.text.isNullOrEmpty()){
                if(passwordFieldRegistration.text.toString() == passwordConfirmationFieldRegistration.text.toString()){
                    registerNewEmail(emailFieldRegistration.text.toString(), passwordFieldRegistration.text.toString())
                }else{
                    Toast.makeText(this, "Password don't match", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "You must fill out all the fields", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendVerificationEmail(){
        val user = firebaseAuth.currentUser
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    Toast.makeText(baseContext, "Verification E-mail sent",
                        Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(baseContext, "Couldn't send Verification E-mail",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerNewEmail(email: String, password: String){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this){task ->
            if (task.isSuccessful){
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                sendVerificationEmail()
                firebaseAuth.signOut()
                loginManager.logOut()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "Successfully Registered with E-mail:$email")})
                finish()
            }else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Registration failed.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "Registration"
    }
}
