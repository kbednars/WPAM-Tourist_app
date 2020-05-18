package com.example.wpam.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.R
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.BlogPost
import com.example.wpam.model.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*
import kotlinx.android.synthetic.main.layout_profile_list_item.view.*

class ProfileRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<UserData> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BlogViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_profile_list_item,
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
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList(list: List<UserData>){
        items = list
    }

    fun addList(list: List<UserData>){
        items = items + list;
    }

    class BlogViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val image = itemView.profile_image
        val name = itemView.profile_name

        fun bind(userData: UserData) {
            name.setText(userData.name)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(userData.profilePicturePath?.let { StorageUtility.pathToReference(it) })
                .into(image)

            itemView.setOnClickListener {
                val navController = itemView?.findNavController()
                if(userData.uid != FirebaseAuth.getInstance().currentUser?.uid) {



                    val bundle = Bundle()
                    bundle.putString("notificationId", userData.uid)
                    navController?.navigate(
                        R.id.action_navigation_search_to_friendProfileFragment,
                        bundle
                    )
                }else{
                    navController.navigate(R.id.navigation_profile)
                }
            }
        }

    }

}