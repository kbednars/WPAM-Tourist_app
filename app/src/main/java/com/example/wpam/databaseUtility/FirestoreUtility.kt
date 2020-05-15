package com.example.wpam.databaseUtility

import android.util.Log
import com.example.wpam.callbacks.*
import com.example.wpam.locationUtility.LocationUtility
import com.example.wpam.model.MarkerInfo
import com.example.wpam.model.PlacePhoto
import com.example.wpam.model.UserData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object FirestoreUtility{
    private val firestoreInstance: FirebaseFirestore by lazy {FirebaseFirestore.getInstance()}
    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("usersData/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    fun initCurrentUserDataIfFirstTime(onComplete: () -> Unit) {
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val newUser = UserData(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                    "", "", mutableListOf(),0)
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

    fun addPlacePhoto(placePhotoPath: String, placeVisitedName:String, description: String=""){
        if (placePhotoPath.isNotBlank() && placeVisitedName.isNotBlank()) {
            currentUserDocRef.collection("placesPhotos").document(placeVisitedName).get()
                .addOnCompleteListener { task ->
                    if (task.result!!.data == null) {
                        val newPlacePhoto = PlacePhoto(placeVisitedName ,description, placePhotoPath, mutableListOf(), Timestamp.now().toDate())
                        currentUserDocRef.collection("placesPhotos").document(placeVisitedName).set(newPlacePhoto)
                        Log.d("FirestoreUtility",newPlacePhoto.toString() + " added to placesPhotos")
                        addPointsToAccount()
                    } else
                        Log.d("FirestoreUtility", "this places had been added")
                }
        }else
            Log.d("FirestoreUtility", "placePhotoPath is blank")
    }

    fun getCurrentUserPlacePhotoPaths(photoPathCallback: PlacesPhotoPathCallback){
        currentUserDocRef.collection("placesPhotos").get().addOnCompleteListener{task->
            if (task.isSuccessful) {
                val list:MutableList<PlacePhoto> = mutableListOf()
                for (document in task.result!!) {
                    list.add(document.toObject(PlacePhoto::class.java))
                }
                photoPathCallback.onCallback(list)
            }
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

    fun getCurrentUserPhotoCollection(onComplete: (MutableList<PlacePhoto>) -> Unit) {
        currentUserDocRef.collection("placesPhotos").get().addOnSuccessListener {
            onComplete(it.toObjects(PlacePhoto::class.java))
        }
    }

    suspend fun getFriendsPlacePhotoPaths(firstPhoto: Int, photoNumber: Int, friendsPhotoCallback: FriendsPhotoCallback){
        var friendsPhotosList: MutableList<Pair<UserData?, PlacePhoto>> = mutableListOf()
       val job = GlobalScope.launch {
            val user = getFriendList()
            for (friend in user!!.friendsAccounts){
                val friendData = getFriendUserData(friend)
                val friendPhotos = getFriendPhotos(friend)
                for(photo in friendPhotos)
                    friendsPhotosList.add(Pair(friendData, photo))
            }
        }
        job.join()
        if (!friendsPhotosList.isEmpty()) {
            friendsPhotosList = friendsPhotosList.toList().sortedByDescending { (_, value) -> value.timeStamp }.toMutableList()
            friendsPhotosList = friendsPhotosList.toList().subList( firstPhoto, if (firstPhoto + photoNumber < friendsPhotosList.size) firstPhoto + photoNumber else friendsPhotosList.size
                ).toMutableList()
                friendsPhotoCallback.onCallback(friendsPhotosList)
            } else
                Log.d("FirestoreUtility:", "getting friends photos unsuccessful")
    }

    suspend private fun getFriendList() = currentUserDocRef.get().await().toObject(UserData::class.java)

    suspend private fun getFriendUserData(UID: String) = firestoreInstance.collection("usersData").document(UID).get().await().toObject(UserData::class.java)

    suspend private fun getFriendPhotos(UID: String) = firestoreInstance.collection("usersData").document(UID).collection("placesPhotos").
            get().await().toObjects(PlacePhoto::class.java)

    fun getUsersByName(name:String, getUsersCallback: GetUsersCallback){
        firestoreInstance.collection("usersData").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val list:MutableList<UserData> = mutableListOf()
                for (document in task.result!!) {
                    if(document.toObject(UserData::class.java).name.contains(name, ignoreCase = true))
                        list.add(document.toObject(UserData::class.java))
                }
                getUsersCallback.onCallback(list)
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

    fun addLike(photoUserUID:String, placeVisitedName:String){
        if(photoUserUID.isNotBlank() && placeVisitedName.isNotBlank()) {
            firestoreInstance.collection("usersData").document(photoUserUID)
                .collection("placesPhotos").document(placeVisitedName).get().addOnSuccessListener {photo->
                    if (!photo.toObject(PlacePhoto::class.java)!!.likes.contains(FirebaseAuth.getInstance().currentUser?.uid)) {
                        firestoreInstance.collection("usersData").document(photoUserUID)
                            .collection("placesPhotos").document(placeVisitedName).update("likes", FieldValue.arrayUnion(FirebaseAuth.getInstance().currentUser?.uid))
                        Log.d("FirestoreUtility","like to photo added")
                    }else
                        Log.d("FirestoreUtility","like had been already given")
                }
        }else
            Log.d("FirestoreUtility", "adding like to photo unsuccessful, because strings are blank")
    }

    fun deleteLike(photoUserUID:String, placeVisitedName:String){
        if(photoUserUID.isNotBlank() && placeVisitedName.isNotBlank()) {
            firestoreInstance.collection("usersData").document(photoUserUID)
                .collection("placesPhotos").document(placeVisitedName).get().addOnSuccessListener {photo->
                    if (photo.toObject(PlacePhoto::class.java)!!.likes.contains(FirebaseAuth.getInstance().currentUser?.uid)) {
                        firestoreInstance.collection("usersData").document(photoUserUID)
                            .collection("placesPhotos").document(placeVisitedName).update("likes", FieldValue.arrayRemove(FirebaseAuth.getInstance().currentUser?.uid))
                        Log.d("FirestoreUtility","like to photo deleted")
                    }else
                        Log.d("FirestoreUtility","there no like from current user")
                }
        }else
            Log.d("FirestoreUtility", "deleting like to photo unsuccessful, because strings are blank")
    }

    fun addPointsToAccount(){
        getCityMarkers(object: MarkerCallback {
            override fun onCallback(list: MutableList<MarkerInfo>) {
                val pointsToAdd = list.size - LocationUtility.getMarkersCountity()
                currentUserDocRef.get().addOnSuccessListener{user ->
                    val actualPoints= user.toObject(UserData::class.java)!!.points
                    val updatedPoints = pointsToAdd + actualPoints
                    currentUserDocRef.update("points", FieldValue.increment(updatedPoints.toLong()))
                }
            }
        })
    }

    fun getUsersRanking(getUsersCallback: GetUsersCallback){
        firestoreInstance.collection("usersData").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                var list:MutableList<UserData> = task.result!!.toObjects(UserData::class.java)
                list = list.toList().sortedByDescending { it.points }.toMutableList()
                getUsersCallback.onCallback(list)
            }
        }
    }
}
