package com.example.wpam

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wpam.databaseUtility.FirestoreUtility
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomePage : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager

    private val RESULT_LOAD_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 42

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

        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        val navController = findNavController(R.id.nav_host_fragment)
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
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_search -> {
                openSearch()
                true
            }
            R.id.action_notifications -> {
                openNotifications()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openNotifications() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.navigation_landmark_list)
    }

    private fun openSearch() {

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

}
