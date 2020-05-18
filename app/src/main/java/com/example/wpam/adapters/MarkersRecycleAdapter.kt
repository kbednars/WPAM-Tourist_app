package com.example.wpam.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.R
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.model.MarkerInfo
import com.example.wpam.ui.points.GetPointsViewModel
import kotlinx.android.synthetic.main.layout_marker_list_item.view.*

class MarkersRecycleAdapter(activity: FragmentActivity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Pair<MarkerInfo, String>> = ArrayList()
    public var activity: FragmentActivity = activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RankingViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_marker_list_item,
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
                holder.bind(items[position], position, activity)
                Log.i("MyTAG", "Se jestem w adapterze")
            }
        }
    }

    fun submitList(rankingList: List<Pair<MarkerInfo, String>>){
        items = rankingList
    }

    fun addList(rankingList: List<Pair<MarkerInfo, String>>){
        items = items + rankingList;
    }

    class RankingViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        val markerImage = itemView.marker_image
        val markerDistance = itemView.marker_distance
        val markerName = itemView.marker_name

        fun bind(marker: Pair<MarkerInfo, String>, position: Int, activity: FragmentActivity) {
            markerDistance.setText(marker.second)
            markerName.setText(marker.first.Name)

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)

            if(marker.first.miniaturePath!!.isNotBlank()) {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(StorageUtility.pathToReference(marker.first.miniaturePath!!))
                    .into(markerImage)
            }
            itemView.setOnClickListener {
                val navController = itemView?.findNavController()
                var viewModel = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!
                viewModel.marker = marker.first
                navController.navigateUp()
            }
        }
    }

}