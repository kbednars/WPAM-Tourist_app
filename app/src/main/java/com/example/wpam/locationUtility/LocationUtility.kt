package com.example.wpam.locationUtility

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.wpam.callbacks.MarkerCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.MarkerInfo
import com.google.android.gms.location.*
import java.util.*
import kotlin.math.absoluteValue

object LocationUtility {
    private lateinit var lastLocation: Location
    private lateinit var actualCity: String
    private lateinit var actualMarkersList: MutableList<MarkerInfo>
    private var actualDistanceToMakers = mutableMapOf<String, Double>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private const val markerDistanceToTakePhoto = 0.05

    fun initLocationSerive(activity: Activity){
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
            Log.d("LocationUtility:","location not granted")
        }
        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.setInterval(15000)
        mLocationRequest.setFastestInterval(5000)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.getLocations()) {
                    if (location != null) {
                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
                        fusedLocationClient.lastLocation.addOnSuccessListener(activity){ location : Location? ->
                            if (location != null){
                                lastLocation = location

                                val gcd = Geocoder(activity, Locale.getDefault())
                                val addresses: List<Address> = gcd.getFromLocation(location.latitude, location.longitude, 1)
                                if (addresses.size > 0) {
                                    Log.d("LocationUtility", "adress found: "+addresses[0].getLocality())
                                    actualCity = addresses[0].getLocality()
                                    Log.d("LocationUtility","distance: "+ distance(
                                        location.latitude,
                                        location.longitude,
                                        50.0,
                                        50.0
                                    ) +" km")
                                }else{
                                    // do your stuff
                                }
                            }
                        }
                    }
                }
            }
        }
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    fun getMarkers(){
        FirestoreUtility.getCurrentUser { user ->
            if (LocationUtility::actualCity.isInitialized) {
                FirestoreUtility.getCityMarkers(object : MarkerCallback {
                    override fun onCallback(list: MutableList<MarkerInfo>) {
                        if (list.isNotEmpty()) {
                            Log.d("LocationUtility", list.toString())
                            actualMarkersList = list
                            val iterator = actualMarkersList.iterator()
                            while (iterator.hasNext()){
                                val item = iterator.next()
                                if(user.visitedPlaces.find({it == item.Name})!=null) {
                                    iterator.remove()
                                    Log.d("LocationUtility", "marker visited "+item.Name)
                                }
                            }
                            calcDistanceToMarkers()
                        } else
                            Log.d("LocationUtility", "city not found")
                    }
                })
            } else {
                Log.d("LocationUtility", "actual city not initialized")
            }
        }
    }

    fun calcDistanceToMarkers(){
        if(LocationUtility::actualMarkersList.isInitialized) {
            actualDistanceToMakers.clear()
            actualMarkersList.forEach{ marker->
                actualDistanceToMakers.put(marker.Name,
                    distance(
                        lastLocation.latitude,
                        lastLocation.longitude,
                        marker.positionX.toDouble(),
                        marker.positionY.toDouble()
                    )
                )
            }
            Log.d("LocationUtility", actualDistanceToMakers.toString())
            val markerPair =
                nearestMarkerPhotoActive()
            if(markerPair!=null)
                Log.d("LocationUtility", "nearest marker to take photo"+markerPair.toString())
        }else{
            Log.d("LocationUtility", "actual marker list not initialized")
        }
    }

    fun getActualCity(): String{
        return actualCity
    }

    fun nearestMarkerPhotoActive():Pair<String, Double>?{
        if(actualDistanceToMakers.isNotEmpty()){
            val result = actualDistanceToMakers.toList().sortedBy { (_,value)->value }[0]
            if(result.second.absoluteValue <= markerDistanceToTakePhoto){
                Log.d("LocationUtility", "distances to markers"+result.toString())
                return result
            }else{
                Log.d("LocationUtility", "markers to far from user location")
                return null
            }
        }else{
            Log.d("LocationUtility", "marker list is empty")
            return null
        }
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(
            deg2rad(
                lat1
            )
        )
                * Math.sin(
            deg2rad(
                lat2
            )
        )
                + (Math.cos(
            deg2rad(
                lat1
            )
        )
                * Math.cos(
            deg2rad(
                lat2
            )
        )
                * Math.cos(
            deg2rad(
                theta
            )
        )))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515 *  1.609344
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}