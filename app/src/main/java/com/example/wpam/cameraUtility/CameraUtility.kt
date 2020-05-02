package com.example.wpam.cameraUtility

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.FileProvider
import java.io.File

object CameraUtility{
    val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 42
    private val FILE_NAME = "photo.jpg"
    lateinit var photoFile: File

    fun runCamera(parentActivity: AppCompatActivity){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(parentActivity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                parentActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(parentActivity, permission, PERMISSION_CODE)
            }else{
                //permission granted
                openCameraView(parentActivity)
            }
        }
    }

    private fun openCameraView(parentActivity: AppCompatActivity) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(FILE_NAME, parentActivity)

        val fileProvider = FileProvider.getUriForFile(parentActivity, "edu.stanford.rkpandey.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(parentActivity.packageManager) != null) {
            parentActivity.startActivityForResult(takePictureIntent, IMAGE_CAPTURE_CODE)
        } else {
            Toast.makeText(parentActivity, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPhotoFile(fileName: String, parentActivity: AppCompatActivity): File {
        val storageDirectory = parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }
}