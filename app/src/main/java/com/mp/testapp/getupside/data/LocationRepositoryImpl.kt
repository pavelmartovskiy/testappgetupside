package com.mp.testapp.getupside.data

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.mp.testapp.getupside.domain.CurrentLocation
import com.mp.testapp.getupside.domain.CurrentLocationResult
import com.mp.testapp.getupside.domain.LocationRepository
import com.mp.testapp.getupside.domain.PermissionsRequest


class LocationRepositoryImpl(private val context: Context) : LocationRepository, LifecycleObserver {

    private val locationLiveData = MutableLiveData<CurrentLocationResult>()

    private val fusedLocationClient = com.google.android.gms.location.LocationServices
            .getFusedLocationProviderClient(context)

    init {
        fusedLocationClient
    }

    @SuppressLint("MissingPermission")
    override fun followLastLocation(): LiveData<CurrentLocationResult> {
        val missedFineLocationGranted = checkAccessFineLocation()
        if (missedFineLocationGranted) {
            locationLiveData.postValue(PermissionsRequest())
        } else {
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.result == null) {
                    locationLiveData.postValue(CurrentLocation(50.508928, 30.485240))
                } else {
                    locationLiveData.postValue(CurrentLocation(it.result.latitude, it.result.longitude))
                }
            }
        }
        return locationLiveData
    }

    private fun checkAccessFineLocation() =
            ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
    }
}