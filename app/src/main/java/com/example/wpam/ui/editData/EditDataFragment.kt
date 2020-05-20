package com.example.wpam.ui.editData

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.wpam.EXTRA_MESSAGE
import com.example.wpam.MainActivity
import com.example.wpam.R
import com.example.wpam.cameraUtility.CameraUtility
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.databaseUtility.StorageUtility
import com.example.wpam.ui.home.HomeViewModel
import com.example.wpam.ui.points.GetPointsViewModel
import java.io.ByteArrayOutputStream


class EditDataFragment : Fragment() {

    private val RESULT_LOAD_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false
    val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 42


    private lateinit var editNameField: TextView
    private lateinit var editDescriptionField: TextView
    private lateinit var userProfileImage: ImageView
    private lateinit var viewModel: GetPointsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_data, container, false)
        viewModel = activity?.let { ViewModelProviders.of(it).get(GetPointsViewModel::class.java) }!!

        editNameField = root.findViewById<View>(R.id.editNameField) as TextView
        editDescriptionField = root.findViewById<View>(R.id.editDescriptionField) as TextView
        val updateUserDataButton = root.findViewById<View>(R.id.setUserDataButton) as Button
       updateUserDataButton.setOnClickListener {
            if (::selectedImageBytes.isInitialized)
                StorageUtility.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                    FirestoreUtility.updateCurrentUserData(
                        editNameField.text.toString(),
                        editDescriptionField.text.toString(), imagePath
                    )
                }
            else
                FirestoreUtility.updateCurrentUserData(
                    editNameField.text.toString(),
                    editDescriptionField.text.toString(), null

                )
            view?.findNavController()?.navigate(R.id.navigation_profile)
            Toast.makeText(activity, "New data set", Toast.LENGTH_LONG).show()
        }

        val takePictureButton = root.findViewById<View>(R.id.takePictureButton) as Button
        takePictureButton.setOnClickListener {
            CameraUtility.runCamera(activity as AppCompatActivity)
        }

           userProfileImage = root.findViewById<View>(R.id.userProfileImage) as ImageView
        userProfileImage.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, RESULT_LOAD_IMAGE)
        }

        FirestoreUtility.initCurrentUserDataIfFirstTime {
            FirestoreUtility.getCurrentUser { user ->
                editNameField.setText(user.name)
                editDescriptionField.setText(user.description)
                print(user.profilePicturePath)
                if (!pictureJustChanged && user.profilePicturePath!!.isNotBlank())
                    Glide.with(this)
                        .load(StorageUtility.pathToReference(user.profilePicturePath))
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.ic_launcher_background)
                        )
                        .into(userProfileImage)
            }
        }

        return root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }


    public override fun onStart() {
        super.onStart()


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraUtility.runCamera(activity as AppCompatActivity)
                }else{
                 Toast.makeText(activity,"Permission Denied", Toast.LENGTH_LONG).show() }
            }
        }


    }

    override fun onResume() {
        super.onResume()
        pictureJustChanged = viewModel.pictureJustChanged
        if(pictureJustChanged){
            viewModel.pictureJustChanged = false
           selectedImageBytes = viewModel.selectedImageBytes
            Glide.with(this)
                .load(selectedImageBytes)
                .into(userProfileImage)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("FriendsData",  "Jestem w activity result")
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            viewModel.pictureJustChanged = true
            viewModel.selectedImageBytes = selectedImageBytes

            /*Glide.with(this)
                .load(selectedImageBytes)
                .into(userProfileImage)
*/

        }
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(CameraUtility.photoFile.absolutePath)

            val outputStream = ByteArrayOutputStream()
            takenImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            viewModel.pictureJustChanged = true
            viewModel.selectedImageBytes = selectedImageBytes

            /*Glide.with(this)
                .load(selectedImageBytes)
                .into(userProfileImage)*/

        }
    }
}
