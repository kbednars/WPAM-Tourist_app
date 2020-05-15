package com.example.wpam.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wpam.EXTRA_MESSAGE
import com.example.wpam.MainActivity
import com.example.wpam.R
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        profileViewModel.text.observe(viewLifecycleOwner, Observer {

        })

        val signOutButton = root.findViewById(R.id.profile_sign_out_button) as Button
        signOutButton.setOnClickListener{
                view: View? -> firebaseAuth.signOut()
            loginManager.logOut()
            Toast.makeText(activity, "You logged out", Toast.LENGTH_LONG).show()
            startActivity(Intent(activity, MainActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, "Zalogowano")})
            activity?.finish()
        }

        return root
    }




}
