package com.example.wpam.databaseUtility

import com.example.wpam.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreUtility{
    private val firestoreInstance: FirebaseFirestore by lazy {FirebaseFirestore.getInstance()}

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("usersData/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    fun initCurrentUserDataIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = UserData(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", null)
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    fun updateCurrentUserData(name: String = "", description: String = "", profilePicturePath: String? = null) {
        val userFieldMap = mutableMapOf<String, Any>()
        if (name.isNotBlank()) userFieldMap["name"] = name
        if (description.isNotBlank()) userFieldMap["description"] = description
        if (profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (UserData) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(UserData::class.java)!!)
            }
    }
}