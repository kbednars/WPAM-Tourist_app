package com.example.wpam.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.EXTRA_MESSAGE
import com.example.wpam.MainActivity
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.adapters.ProfileLandmarkListAdapter
import com.example.wpam.callbacks.FriendsPhotoCallback
import com.example.wpam.callbacks.PhotoCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.BlogPost
import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_display_logged.*

class ProfileFragment : Fragment() {
    private lateinit var root: View
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var profileLandmarkListAdapter: ProfileLandmarkListAdapter
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        loginManager = LoginManager.getInstance()

        profileViewModel = activity?.let { ViewModelProviders.of(it).get(ProfileViewModel::class.java) }!!

        root = inflater.inflate(R.layout.fragment_profile, container, false)



        recyclerView= root.findViewById<RecyclerView>(R.id.profile_recycler_view)
        linLayoutManager = LinearLayoutManager(activity)

        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            profileLandmarkListAdapter = profileViewModel.profileLandmarkListAdapter
            adapter =  profileLandmarkListAdapter
        }


        val profileName = root.findViewById<TextView>(R.id.profile_username)
        val profileImage = root.findViewById<ImageView>(R.id.profile_picture)
        val profileDescription = root.findViewById<TextView>(R.id.profile_description)
        val pointsCount = root.findViewById<TextView>(R.id.profile_points_count)
        val friendsCount = root.findViewById<TextView>(R.id.profile_friends_count)
        val friends = root.findViewById<TextView>(R.id.profile_friends)

        friends.setOnClickListener{
            val navController = view?.findNavController()
            navController?.navigate(R.id.action_navigation_profile_to_friendListFragment)
        }

        FirestoreUtility.getCurrentUser { user ->
                profileName.setText(user.name)
                profileDescription.setText(user.description)
                friendsCount.setText(user.friendsAccounts.size.toString())
                pointsCount.setText(user.points.toString())
                print(user.profilePicturePath)
                if (user.profilePicturePath!!.isNotBlank())
                    Glide.with(this)
                        .load(StorageUtility.pathToReference(user.profilePicturePath))
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.ic_launcher_background))
                        .into(profileImage)
        }


        val signOutButton = root.findViewById(R.id.profile_sign_out_button) as Button
        signOutButton.setOnClickListener{
                view: View? -> firebaseAuth.signOut()
            loginManager.logOut()
            Toast.makeText(activity, "You logged out", Toast.LENGTH_LONG).show()
            startActivity(Intent(activity, MainActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, "Zalogowano")})
            activity?.finish()
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = linLayoutManager.itemCount
                val lastVisible = linLayoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItemCount == lastVisible + 1) {
                    recyclerView.removeOnScrollListener(scrollListener)
                    addDataSet(0, 10)
                }
            }
        }

        recyclerView.addOnScrollListener(scrollListener)
        if(profileLandmarkListAdapter.getItemCount() == 0)
            addDataSet(0, 10)

        return root
    }

    private fun addDataSet(begin : Int, end : Int) {
        val data = ArrayList<BlogPost>()

        FirestoreUtility.getCurrentUserPhotoCollection(begin,end,object: PhotoCallback {
            override fun onCallback(list: MutableList<PlacePhoto>) {
                for(photos in list){
                    var blogPost = BlogPost(photos.name, photos.description, photos.placePhotoPath, "me", photos.likes.size,
                        photos.likes.contains(FirebaseAuth.getInstance().currentUser?.uid.toString()) , "me" )
                    data.add(blogPost)
                }
                profileLandmarkListAdapter.submitList(data)
                recyclerView.addOnScrollListener(scrollListener)
                activity?.runOnUiThread({
                    profileLandmarkListAdapter.notifyDataSetChanged()})
            }
        })


    }



    override fun onPause() {
        super.onPause()
        profileViewModel.linearLayoutManager.value = linLayoutManager.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        linLayoutManager.onRestoreInstanceState(profileViewModel.linearLayoutManager.value)
    }

}
