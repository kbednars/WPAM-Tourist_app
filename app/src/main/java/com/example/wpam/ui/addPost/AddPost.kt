package com.example.wpam.ui.addPost

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
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.wpam.R
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.ui.points.GetPointsViewModel


class AddPost : Fragment() {

    companion object {
        fun newInstance() = AddPost()
    }

    private lateinit var viewModel: GetPointsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_post, container, false)

        val image = root.findViewById<ImageView>(R.id.add_post_image)
        val text = root.findViewById<TextView>(R.id.add_post_description)
        val button = root.findViewById<Button>(R.id.add_post_submit_button)
        viewModel = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!


        Glide.with(this)
            .load(viewModel.selectedImageBytes)
            .into(image)


        button.setOnClickListener{
            StorageUtility.uploadPlacePhoto(viewModel.selectedImageBytes){imagePath ->
                viewModel.marker?.Name?.let { it1 ->
                    Log.d(
                        "FirestoreUtility",
                        "Se zdjatka updatuje"
                    )
                    FirestoreUtility.addPlacePhoto(imagePath,
                        it1, text.text.toString())

                    viewModel.marker = null
                    view?.findNavController()?.navigate(R.id.navigation_profile)
                }
            }
            Log.d(
                "FirestoreUtility",
                "A tutej se wracam"
            )
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

}
