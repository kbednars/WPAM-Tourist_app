package com.example.wpam.ui.points

import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.R
import com.example.wpam.cameraUtility.CameraUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.locationUtility.LocationUtility
import java.util.*
import kotlin.concurrent.schedule


class GetPointsFragment : Fragment() {


    private lateinit var viewModel: GetPointsViewModel

    lateinit var context: AppCompatActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("MyTAG", "Jestem w onCreateView")
        val root = inflater.inflate(R.layout.fragment_get_points, container, false)
        viewModel = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!

        val chooseMarkerButton = root.findViewById(R.id.get_points_button_choose_marker) as Button
        chooseMarkerButton.setOnClickListener{
            val navController = view?.findNavController()

            viewModel.search.value = true
            val bundle = Bundle()
            navController?.navigate(R.id.action_navigation_get_points_to_search, bundle)

        }

        val image = root.findViewById(R.id.get_points_marker_photo) as ImageView
        val title = root.findViewById(R.id.get_points_marker_title) as TextView
        val description = root.findViewById(R.id.get_points_description) as TextView
        val distance = root.findViewById(R.id.get_points_distance) as TextView

        val makePhotoButton = root.findViewById(R.id.get_points_make_photo) as Button
        makePhotoButton.setOnClickListener {
            Log.i("MyTAG", "Button CLICKED")
            CameraUtility.runCamera(activity as AppCompatActivity)
        }
        makePhotoButton.isVisible = false
        makePhotoButton.isClickable = false

        if(viewModel.marker != null){
            title.setText(viewModel.marker!!.Name)
            description.setText(viewModel.marker!!.Description)
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
            Glide.with(this)
                .applyDefaultRequestOptions(requestOptions)
                .load(viewModel.marker!!.miniaturePath?.let { StorageUtility.pathToReference(it) })
                .into(image)
            LocationUtility.chooseMarkerToTrack(viewModel.marker!!)
            Timer("SettingUp", false).schedule(0, 1000) {
                val info = LocationUtility.choosedMarkerDist()
                activity?.runOnUiThread({
                    distance.setText(info.first.toString())
                    makePhotoButton.isVisible = info.second
                    makePhotoButton.isClickable = info.second
                })
            }
        }




        return root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.i("MyTAG", viewModel.dupa.value)
        if(viewModel.search_pause.value == false) {
            viewModel.search.value = false
        }
        if(viewModel.search.value == true){
            val navController = view?.findNavController()
            viewModel.search.value = true
            val bundle = Bundle()
            bundle.putString("notificationId", "dupa")
            navController?.navigate(R.id.action_navigation_get_points_to_search, bundle)
        }
        Log.i("MyTAG", "Jestem w ActivityCreated")
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)

        Log.i("MyTAG", "Jestem w Save")
        state.putString("text", "lol")
    }

    override fun onPause() {
        super.onPause()
        Log.i("MyTAG", "Jestem w pause")
        Log.i("MyTAG", viewModel.dupa.value)

    }

    override fun onResume() {
        super.onResume()
        Log.i("MyTAG", "Jestem w resume")
        Log.i("MyTAG", viewModel.marker.toString())
        if(viewModel.pictureJustChanged){
            val navCont = view?.findNavController()
            viewModel.pictureJustChanged = false
            navCont?.navigate(R.id.action_navigation_get_points_to_addPost)
        }

    }


}
