package com.mp.testapp.getupside.presentation.main

import android.graphics.RectF
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.mp.testapp.getupside.domain.*


class MainViewModel(
        private val gisRepository: ArcGisRepository,
        private val locationRepository: LocationRepository,
        private val realmRepository: RealmRepository
) : ViewModel() {

    private val requestUserLocationLiveData = MutableLiveData<Boolean>()
    private val requestBufferedFoodPointsLiveData = MutableLiveData<Boolean>()
    private val requestRefreshFoodPointsLiveData = MutableLiveData<Boolean>()
    private val requestVisibleFoodPointsLiveData = MutableLiveData<RectF>()


    fun followVisibleFoodPoints() = visibleFoodPointsLiveData
    fun followFoodPoints() = foodPointsLiveData
    fun followUserLocation() = userLocationLiveData


    fun requestUserLocation() {
        requestUserLocationLiveData.postValue(true)
    }

    fun requestFoodPoints(left: Double, top: Double, right: Double, bottom: Double) {
        requestVisibleFoodPointsLiveData.postValue(RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()))
    }

    fun requestFoodPoints() {
        requestBufferedFoodPointsLiveData.postValue(true)
    }

    fun requestRefreshFoodPoints() {
        requestRefreshFoodPointsLiveData.postValue(true)
    }

    private val userLocationLiveData = MediatorLiveData<CurrentLocationResult>().apply {
        addSource(locationRepository.followLastLocation()) { postValue(it) }
        addSource(Transformations.switchMap(requestUserLocationLiveData) { locationRepository.followLastLocation() }) { postValue(it) }
    }

    private val successUserLocationLiveData = MediatorLiveData<CurrentLocation>().apply {
        addSource(locationRepository.followLastLocation()) { if (it is CurrentLocation) postValue(it) }
        addSource(Transformations.switchMap(requestUserLocationLiveData) { locationRepository.followLastLocation() }) { if (it is CurrentLocation) postValue(it) }
    }

    private val foodPointsLiveData = MediatorLiveData<FoodPointsSearchResult>().apply {
        addSource(Transformations.switchMap(requestBufferedFoodPointsLiveData) { realmRepository.getAllPoints() }) { postValue(FoodPointsSearchSuccess(it)) }
    }

    private val visibleFoodPointsLiveData = MediatorLiveData<FoodPointsSearchResult>().apply {

        var userLocation: CurrentLocation? = null
        var searchResult: FoodPointsSearchResult? = null

        val requestFoodPointsPointsLiveData = MutableLiveData<CurrentLocation>()

        fun update() {
            val ul = userLocation
            val sr = searchResult

            if (ul == null || sr == null) {
                return
            }

            when (sr) {
                is FoodPointsSearchSuccess -> when {
                    sr.points.isNotEmpty() -> postValue(sr)
                    sr.points.isEmpty() -> requestFoodPointsPointsLiveData.postValue(ul)
                }
                is FoodPointsSearchError -> postValue(sr)
            }


        }

        fun createVisibleFoodPointsFromBufferLiveData() = Transformations.switchMap(requestVisibleFoodPointsLiveData) { bounds ->
            Transformations.map(realmRepository.getAllPoints()) { foodPoints ->
                foodPoints
                        .filter { foodPoint ->
                            bounds
                                    .contains(
                                            foodPoint.longitude.toFloat(),
                                            foodPoint.latitude.toFloat()
                                    )
                        }
            }
        }

        addSource(successUserLocationLiveData) { result ->
            userLocation = result
            update()
        }

        addSource(createVisibleFoodPointsFromBufferLiveData()) { foodPoints ->
            searchResult = FoodPointsSearchSuccess(foodPoints)
            update()
        }

        fun updateResult(result: FoodPointsSearchResult) {
            searchResult = result

            if (result is FoodPointsSearchSuccess) {
                realmRepository.deleteAll()
                realmRepository.createOrUpdateAll(result.points)
            }

            update()
        }

        addSource(
                Transformations.switchMap(
                        Transformations.switchMap(requestRefreshFoodPointsLiveData) {
                            successUserLocationLiveData
                        }
                ) { location -> gisRepository.getFoodPoint(location.latitude, location.longitude) }
        ) {
            result -> updateResult(result)
        }

        addSource(
                Transformations.switchMap(
                        requestFoodPointsPointsLiveData
                ) { location -> gisRepository.getFoodPoint(location.latitude, location.longitude) }
        ) { result -> updateResult(result) }

    }



}