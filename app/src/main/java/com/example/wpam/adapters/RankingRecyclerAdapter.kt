package com.example.wpam.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.R
import com.example.wpam.model.BlogPost
import com.example.wpam.model.RankingItem
import kotlinx.android.synthetic.main.layout_ranking_list_item.view.*

class RankingRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: List<RankingItem> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RankingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_ranking_list_item,
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

            is RankingViewHolder ->{
                holder.bind(items.get(position), position)
            }
        }
    }

    fun submitList(rankingList: List<RankingItem>){
        items = rankingList
    }

    fun addList(rankingList: List<RankingItem>){
        items = items + rankingList;
    }

    class RankingViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val rankingProfileImage = itemView.ranking_profile_image
        val rankingPoints = itemView.ranking_points
        val rankingProfileName = itemView.ranking_profile_name
        val rankingPosition = itemView.ranking_position

        fun bind(rankingItem: RankingItem, position: Int) {
            rankingPoints.setText(rankingItem.points + " pts")
            rankingProfileName.setText(rankingItem.username)
            rankingPosition.setText(position.toString())

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)

            Glide.with(itemView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(rankingItem.image).into(rankingProfileImage)
        }
    }

}