package com.example.wpam

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_display_logged.*
import java.io.ByteArrayOutputStream


class DisplayLoggedActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager

    private val RESULT_LOAD_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

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

        userProfileImage.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        // Get the Intent that started this activity and extract the string
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val signOutButton = findViewById<View>(R.id.sign_out_button) as Button
        signOutButton.setOnClickListener{
                view: View? -> firebaseAuth.signOut()
                loginManager.logOut()
                Toast.makeText(this, "You logged out", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "Zalogowano")})
            finish()
        }

        val updateUserDataButton = findViewById<View>(R.id.setUserDataButton) as Button
        updateUserDataButton.setOnClickListener {
            if (::selectedImageBytes.isInitialized)
                StorageUtility.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                    FirestoreUtility.updateCurrentUserData(editNameField.text.toString(),
                        editDescriptionField.text.toString(), imagePath)
                }
            else
                FirestoreUtility.updateCurrentUserData(editNameField.text.toString(),
                    editDescriptionField.text.toString(), null)
            Toast.makeText(this, "Setting new data", Toast.LENGTH_LONG).show()
        }
    }

    public override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this.authStateListener)

        FirestoreUtility.initCurrentUserDataIfFirstTime {
            FirestoreUtility.getCurrentUser { user ->
                editNameField.setText(user.name)
                editDescriptionField.setText(user.description)
                print(user.profilePicturePath)
                if (!pictureJustChanged && user.profilePicturePath != null)
                    Glide.with(this)
                        .load(StorageUtility.pathToReference(user.profilePicturePath))
                        .apply(RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background))
                        .into(userProfileImage)
            }
        }
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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(this.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            Glide.with(this)
                .load(selectedImageBytes)
                .into(userProfileImage)

            pictureJustChanged = true
        }
    }

    companion object {
        const val TAG = "DisplayLoggedActivity"
    }
}
