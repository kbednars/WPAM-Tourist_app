package com.example.wpam.ui.points

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.wpam.R
import com.example.wpam.cameraUtility.CameraUtility
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import kotlinx.android.synthetic.main.activity_display_logged.*
import kotlinx.android.synthetic.main.activity_main.*


class GetPointsFragment : Fragment() {


    private lateinit var viewModel: GetPointsViewModel
    private lateinit var textView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("MyTAG", "Jestem w onCreateView")
        val root = inflater.inflate(R.layout.fragment_get_points, container, false)
        textView = root.findViewById(R.id.getPointsEditText)

        val takePictureButton = root.findViewById(R.id.getPointsTakePictureButton) as Button
        takePictureButton.setOnClickListener{
            textView.text = "CLICKED";
        }


        val updateUserDataButton = root.findViewById(R.id.setUserDataButton2) as Button
        updateUserDataButton.setOnClickListener {
            Log.i("MyTAG", "Button CLICKED")
            if (textView.text.toString() == "0"){
                    takePictureButton.isClickable = true
                    takePictureButton.isVisible = true
                }else{
                    takePictureButton.isClickable = false
                    takePictureButton.isVisible = false
            }

        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!
        Log.i("MyTAG", viewModel.dupa.value)
        textView.text = viewModel.dupa.value;
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
        viewModel.dupa.value = textView.text.toString()

        Log.i("MyTAG", viewModel.dupa.value)

    }

    override fun onResume() {
        Log.i("MyTAG", "Jestem w resume")

        super.onResume()
    }


}
