package com.mp.testapp.getupside.data

import androidx.lifecycle.*
import com.mp.testapp.getupside.domain.LocationRepository
import com.mp.testapp.getupside.domain.UserLocation

class LocationRepositoryImpl() : LocationRepository, LifecycleObserver {

    private val locationLiveData = MutableLiveData<UserLocation>()

    init {
        locationLiveData.postValue(UserLocation(48.805629, 2.487803))
    }

    override fun lastLocation(): LiveData<UserLocation> = locationLiveData


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
    }
}