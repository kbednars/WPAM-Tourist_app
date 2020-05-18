package com.example.wpam.locationUtility

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.wpam.callbacks.GetMarkersCallback
import com.example.wpam.callbacks.MarkerCallback
import com.example.wpam.databaseUtility.FirestoreUtility
import com.example.wpam.model.MarkerInfo
import com.google.android.gms.location.*
import java.util.*

object LocationUtility {
    private lateinit var lastLocation: Location
    private lateinit var actualCity: String
    private lateinit var actualMarkersList: MutableList<Pair<MarkerInfo, String>>
    private var choosedMarker:MarkerInfo? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private const val markerDistanceToTakePhoto = 0.1

    fun initLocationSerive(activity: Activity){
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
            Log.d("LocationUtility:","location not granted")
        }
        val mLocationRequest: LocationRequest = LocationRequest.create()
        mLocationRequest.setInterval(1000)
        mLocationRequest.setFastestInterval(500)
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
                                    actualCity = addresses[0].getLocality()
                                }
                            }
                        }
                    }
                }
            }
        }
        LocationServices.getFusedLocationProviderClient(activity).requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    fun getMarkersCountity():Int{
        return actualMarkersList.size
    }

    fun getMarkers(getMarkersCallback: GetMarkersCallback){
        FirestoreUtility.getCurrentUserPlacePhotoPaths { col ->
            if (LocationUtility::actualCity.isInitialized) {
                FirestoreUtility.getCityMarkers(object : MarkerCallback {
                    override fun onCallback(list: MutableList<MarkerInfo>) {
                        if (list.isNotEmpty()) {
                            Log.d("LocationUtility", list.toString())
                            var iterator = list.iterator()
                            while (iterator.hasNext()){
                                val item = iterator.next()
                                for (doc in col){
                                    if(doc.name == item.Name) {
                                        iterator.remove()
                                        Log.d("LocationUtility", "marker visited " + item.Name)
                                        break
                                    }
                                }
                            }
                            iterator = list.iterator()
                            actualMarkersList = mutableListOf()
                            while (iterator.hasNext()){
                                val item = iterator.next()
                                actualMarkersList.add(Pair(item, distance(lastLocation.latitude, lastLocation.longitude,
                                item.positionX.toDouble(), item.positionY.toDouble()).toString()))
                            }
                            getMarkersCallback.onCallback(actualMarkersList)
                        } else
                            Log.d("LocationUtility", "city not found")
                    }
                })
            } else {
                Log.d("LocationUtility", "actual city not initialized")
            }
        }
    }

    fun getActualCity(): String{
        return actualCity
    }

    fun chooseMarkerToTrack(markerToTrack:MarkerInfo){
        choosedMarker = markerToTrack
    }

    fun deleteMarkerToTrack(){
        choosedMarker = null
    }

    fun getDistToTrackedMarker():Double{
        return distance(lastLocation.latitude, lastLocation.longitude, choosedMarker!!.positionX.toDouble(), choosedMarker!!.positionY.toDouble())
    }

    fun choosedMarkerDist():Pair<Double,Boolean>{
        if(choosedMarker != null){
            val distanceToChoosedMarker = getDistToTrackedMarker()
            if(distanceToChoosedMarker <= markerDistanceToTakePhoto){
                Log.d("LocationUtility", "distances to choosed marker " + distanceToChoosedMarker.toString())
                return Pair(distanceToChoosedMarker, true)
            }else{
                Log.d("LocationUtility", "marker to far from user location")
                return Pair(distanceToChoosedMarker, false)
            }
        }else{
            Log.d("LocationUtility", "marker to track is null")
            return Pair(-1.0, false)
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