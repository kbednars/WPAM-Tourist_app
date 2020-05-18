package com.example.wpam

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.wpam.cameraUtility.CameraUtility
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.locationUtility.LocationUtility
import com.example.wpam.ui.editData.EditDataViewModel
import com.example.wpam.ui.points.GetPointsViewModel
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_display_logged.*
import java.io.ByteArrayOutputStream

class HomePage : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager

    private val RESULT_LOAD_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 42
    private lateinit var navController : NavController

    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        LocationUtility.initLocationSerive(this)
        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_get_points, R.id.navigation_ranking, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

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
            R.id.action_log_out -> {
                openSearch()
                true
            }
            R.id.action_edit_data -> {
                openNotifications()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openNotifications() {
        navController.navigate(R.id.navigation_edit_data)
    }

    private fun openSearch() {
        firebaseAuth.signOut()
        loginManager.logOut()
        Toast.makeText(this, "You logged out", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "Zalogowano")})
        this.finish()
    }

    public override fun onStart() {
        super.onStart()
        firebaseAuth.removeAuthStateListener(this.authStateListener)
        FirestoreUtility.initCurrentUserDataIfFirstTime {
        }
    }


    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this.authStateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("FriendsData",  "Jestem w activity result")
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(this.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            val viewModel = this.let { ViewModelProviders.of(it).get(EditDataViewModel::class.java) }!!

            viewModel.pictureJustChanged = true
            viewModel.selectedImageBytes = selectedImageBytes

            val viewModel2 = this.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!

            viewModel2.pictureJustChanged = true
            viewModel2.selectedImageBytes = selectedImageBytes

        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(CameraUtility.photoFile.absolutePath)

            val outputStream = ByteArrayOutputStream()
            takenImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            val viewModel = this.let { ViewModelProviders.of(it).get(EditDataViewModel::class.java) }!!

            viewModel.pictureJustChanged = true
            viewModel.selectedImageBytes = selectedImageBytes

            val viewModel2 = this.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!

            viewModel2.pictureJustChanged = true
            viewModel2.selectedImageBytes = selectedImageBytes

        }
    }

}
