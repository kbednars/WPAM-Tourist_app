package com.example.wpam.ui.editData

import androidx.lifecycle.ViewModel

class EditDataViewModel : ViewModel() {
    public lateinit var selectedImageBytes: ByteArray
    public  var pictureJustChanged = false
}
