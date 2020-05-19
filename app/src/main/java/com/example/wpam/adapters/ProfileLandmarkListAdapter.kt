package com.example.wpam.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.R
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.BlogPost
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.layout_profile_landmark_list_item.view.*

class ProfileLandmarkListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<BlogPost> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BlogViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_profile_landmark_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){


            is BlogViewHolder ->{
                holder.bind(items.get(position), position)
            }
        }
    }

    fun submitList(blogList: List<BlogPost>){
        items = blogList
    }

    fun addList(blogList: List<BlogPost>){
        items = items + blogList;
    }

    class BlogViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val image   : ImageView = itemView.profile_landmark_list_image
        val title : TextView = itemView.profile_landmark_list_title

        fun bind(blogPost: BlogPost,position: Int) {
            title.setText(blogPost.title)
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                if(blogPost.image!!.isNotBlank()) {
                    Glide.with(itemView.context)
                        .applyDefaultRequestOptions(requestOptions)
                        .load(blogPost.image?.let { StorageUtility.pathToReference(it) })
                        .into(image)
                }
            image.setOnClickListener(){
                val navController = itemView?.findNavController()


                val bundle = Bundle()
                bundle.putString("notificationId", position.toString())
                bundle.putString("uid", blogPost.uid)
                bundle.putString("username", blogPost.username)
                if(blogPost.uid == FirebaseAuth.getInstance().currentUser?.uid.toString())
                    navController?.navigate(R.id.action_navigation_profile_to_photoListFragment, bundle)
                else
                    navController?.navigate(R.id.action_friendProfileFragment_to_photoListFragment, bundle)

            }
        }

    }

}