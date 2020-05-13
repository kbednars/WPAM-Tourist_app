package com.example.wpam.databaseUtility

import android.util.Log
import com.example.wpam.locationUtility.LocationUtility
import com.example.wpam.callbacks.MarkerCallback
import com.example.wpam.callbacks.PlacesPhotoPathCallback
import com.example.wpam.callbacks.UsersByNameCallback
import com.example.wpam.model.MarkerInfo
import com.example.wpam.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

object FirestoreUtility{
    private val firestoreInstance: FirebaseFirestore by lazy {FirebaseFirestore.getInstance()}

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("usersData/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    fun initCurrentUserDataIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = UserData(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", null, mutableListOf(), mutableListOf(), mutableListOf())
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

    fun markerMarkVisited(name: String){
        if (name.isNotBlank()) {
            currentUserDocRef.update("visitedPlaces", FieldValue.arrayUnion(name))
            Log.d("FirestoreUtility", "place "+name+" added to visitedPlaces")
        }
        Log.d("FirestoreUtility", "name of visited Places is blank")
    }

    fun addPlacePhoto(placePhotoPath: String){
        if (placePhotoPath.isNotBlank()) {
            currentUserDocRef.update("placesPhotoPaths", FieldValue.arrayUnion(placePhotoPath))
            Log.d("FirestoreUtility", "path "+placePhotoPath+" added to placePhotoPaths")
        }
        Log.d("FirestoreUtility", "placePhotoPath is blank")
    }

    fun getUserPlacePhotoPaths(photoPathCallback: PlacesPhotoPathCallback){
        getCurrentUser { user->
            val list = user.placesPhotoPaths
            photoPathCallback.onCallback(list)

        }
    }

    fun addFriendAccount(accountFriendUID: String){
        if (accountFriendUID.isNotBlank()) {
            getCurrentUser { user ->
                if(!user.friendsAccounts.contains(accountFriendUID)) {
                    currentUserDocRef.update(
                        "friendsAccounts",
                        FieldValue.arrayUnion(accountFriendUID)
                    )
                    Log.d(
                        "FirestoreUtility",
                        "friend with UID " + accountFriendUID + " added to friendsAccounts"
                    )
                }
            }
        }
        Log.d("FirestoreUtility", "accountFriendUID is blank")
    }

    fun getCurrentUser(onComplete: (UserData) -> Unit) {
        currentUserDocRef.get()
            .addOnSuccessListener {
                onComplete(it.toObject(UserData::class.java)!!)
            }
    }

    fun getUsersByName(name:String, usersByNameCallback: UsersByNameCallback){
        firestoreInstance.collection("usersData").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val list:MutableList<UserData> = mutableListOf()
                for (document in task.result!!) {
                    if(document.toObject(UserData::class.java).name.contains(name, ignoreCase = true))
                        list.add(document.toObject(UserData::class.java))
                }
                usersByNameCallback.onCallback(list)
            }
        }
    }

    fun getCityMarkers(markerCallback: MarkerCallback) {
        firestoreInstance.collection(LocationUtility.getActualCity()).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val list:MutableList<MarkerInfo> = mutableListOf()
                for (document in task.result!!) {
                    list.add(document.toObject(MarkerInfo::class.java))
                }
                markerCallback.onCallback(list)
            }
        }
    }
}