package com.example.wpam.ui.friendProfile

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.EXTRA_MESSAGE
import com.example.wpam.MainActivity
import com.example.wpam.R
import com.example.wpam.TopSpacingItemDecoration
import com.example.wpam.adapters.ProfileLandmarkListAdapter
import com.example.wpam.callbacks.GetUserByIdCallback
import com.example.wpam.callbacks.PhotoCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.BlogPost
import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData
import com.example.wpam.ui.profile.ProfileViewModel
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth


class FriendProfileFragment : Fragment() {
    private lateinit var root: View
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var loginManager: LoginManager
    private lateinit var profileLandmarkListAdapter: ProfileLandmarkListAdapter
    private lateinit var linLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var isFriend : Boolean = false
    private lateinit var scrollListener: RecyclerView.OnScrollListener


    private lateinit var viewModel: FriendProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root =  inflater.inflate(R.layout.fragment_friend_profile, container, false)
        linLayoutManager = LinearLayoutManager(activity)
        profileLandmarkListAdapter = ProfileLandmarkListAdapter()
        recyclerView = root.findViewById<RecyclerView>(R.id.friend_profile_recycle_view)
        val bundle = this.arguments
        val uid = (bundle?.getString("notificationId") ?: "nobody")

        recyclerView.apply{
            layoutManager = linLayoutManager
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter =  profileLandmarkListAdapter
        }

        val addFriendButton = root.findViewById(R.id.friend_profile_add_to_friends_button) as Button

        FirestoreUtility.getCurrentUser { user ->
            isFriend = user.friendsAccounts.contains(uid)
            if(isFriend){
                addFriendButton.setText("Remove Friend")
            }else{
                addFriendButton.setText("Add Friend")
            }
        }



        addFriendButton.setOnClickListener{
            if(isFriend){
                isFriend = false
                addFriendButton.setText("Add Friend")

                Log.i("MyTAG", "Usuwam")
            }else{
                isFriend = true
                //FirestoreUtility.addFriendAccount(uid)
                Log.i("MyTAG", "Dodaje")
                addFriendButton.setText("Remove Friend")
            }
        }


        val profileName = root.findViewById<TextView>(R.id.friend_profile_username)
        val profileImage = root.findViewById<ImageView>(R.id.friend_profile_picture)
        val profileDescription = root.findViewById<TextView>(R.id.friend_profile_description)
        val pointsCount = root.findViewById<TextView>(R.id.friend_profile_points_count)
        val friendsCount = root.findViewById<TextView>(R.id.friend_profile_friends_count)

        if (uid != null) {
            FirestoreUtility.getUserDataById(uid, object:
                GetUserByIdCallback {
                override fun onCallback(userData: UserData) {
                    profileName.setText(userData.name)
                    profileDescription.setText(userData.description)
                    friendsCount.setText(userData.friendsAccounts.size.toString())
                    pointsCount.setText(userData.points.toString())
                    print(userData.profilePicturePath)
                    if (userData.profilePicturePath!!.isNotBlank())
                        Glide.with(this@FriendProfileFragment)
                            .load(StorageUtility.pathToReference(userData.profilePicturePath))
                            .apply(
                                RequestOptions()
                                    .placeholder(R.drawable.ic_launcher_background))
                            .into(profileImage)
                }
            })
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
                    var blogPost = BlogPost(photos.name, photos.description, photos.placePhotoPath, "friend", photos.likes.size,
                        photos.likes.contains(FirebaseAuth.getInstance().currentUser?.uid.toString()) , "friend" )
                    data.add(blogPost)
                }
                profileLandmarkListAdapter.submitList(data)
                recyclerView.addOnScrollListener(scrollListener)
                activity?.runOnUiThread({
                    profileLandmarkListAdapter.notifyDataSetChanged()})
            }
        })


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FriendProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
