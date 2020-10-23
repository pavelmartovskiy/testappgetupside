package com.mp.testapp.getupside.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mp.testapp.getupside.domain.*

class MapViewModel(
        private val gisRepository: ArcGisRepository,
        private val locationRepository: LocationRepository,
        private val realmRepository: RealmRepository
) : ViewModel() {
    fun getFoodPoint(): LiveData<List<FoodPoint>> =
            Transformations.switchMap(locationRepository.lastLocation()) {
                gisRepository.getFoodPoint(it.latitude, it.longitude)
            }

    fun getUserLocation() : LiveData<UserLocation> = locationRepository.lastLocation()
}