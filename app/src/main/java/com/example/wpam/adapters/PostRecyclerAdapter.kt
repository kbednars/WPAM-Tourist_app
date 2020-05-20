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
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.BlogPost
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*

class PostRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<BlogPost> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BlogViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_blog_list_item,
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

    fun submitList(blogList: List<BlogPost>){
        items = blogList
    }

    fun addList(blogList: List<BlogPost>){
        items = items + blogList;
    }

    class BlogViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val blogImage = itemView.blog_image
        val blogTitle = itemView.blog_title
        val blogAuthor = itemView.blog_author
        val bloglikes = itemView.blog_likes
        val blogLikeButton = itemView.blog_like_button
        val blogDescription = itemView.blog_description


        fun bind(blogPost: BlogPost) {
            blogTitle.setText(blogPost.title)
            blogAuthor.setText(blogPost.username)
            bloglikes.setText(blogPost.likes.toString())
            blogDescription.setText(blogPost.body)

            if (blogPost.isliked)
                blogLikeButton.setImageResource(R.drawable.ic_thumb_up_blue_24dp)
            else
                blogLikeButton.setImageResource(R.drawable.ic_thumb_up_grey_24dp)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
            if (blogPost.image!!.isNotBlank()) {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(StorageUtility.pathToReference(blogPost.image))
                    .into(blogImage)
            }
            blogTitle.setOnClickListener {

                Log.i("MyTAG", "TITLE CLICKED " + blogTitle.text)
            }

            blogAuthor.setOnClickListener {
                val navController = itemView?.findNavController()
                Log.i("MyTAG", navController.currentDestination?.label.toString())
                if (navController.currentDestination?.label.toString() == "Home" && blogPost.uid != FirebaseAuth.getInstance().currentUser?.uid) {
                    val bundle = Bundle()
                    bundle.putString("notificationId", blogPost.uid)
                    navController?.navigate(
                        R.id.action_navigation_home_to_friendProfileFragment,
                        bundle
                    )
                }
            }
            blogLikeButton.setOnClickListener {
                if (blogPost.uid != FirestoreUtility.getCurrentUserUid()) {
                    if (blogPost.isliked) {
                        blogPost.likes = blogPost.likes - 1
                        bloglikes.setText((blogPost.likes).toString())
                        blogPost.isliked = false
                        FirestoreUtility.deleteLike(blogPost.uid, blogPost.title)
                        Log.i("MyTAG", "Se usune lajka")
                        blogLikeButton.setImageResource(R.drawable.ic_thumb_up_grey_24dp)
                    } else {
                        Log.i("MyTAG", blogPost.uid)
                        blogLikeButton.setImageResource(R.drawable.ic_thumb_up_blue_24dp)
                        Log.i("MyTAG", blogPost.title)
                        FirestoreUtility.addLike(blogPost.uid, blogPost.title)
                        blogPost.likes = blogPost.likes + 1
                        bloglikes.setText((blogPost.likes).toString())
                        blogPost.isliked = true
                        Log.i("MyTAG", "Se dodam Lajka")
                    }
                }
            }
        }
    }


}