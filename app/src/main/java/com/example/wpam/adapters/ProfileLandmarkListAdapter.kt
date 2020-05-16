package com.example.wpam.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.R
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.BlogPost
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
        val image   : ImageView = itemView.profile_landmark_list_image
        val title : TextView = itemView.profile_landmark_list_title

        fun bind(blogPost: BlogPost) {

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
                Log.d("Zdjatko klikniete: ",title.toString())
            }





        }


    }

}