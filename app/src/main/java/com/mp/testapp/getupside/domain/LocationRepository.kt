package com.mp.testapp.getupside.domain

import androidx.lifecycle.LiveData

interface LocationRepository {
    fun followLastLocation(): LiveData<CurrentLocationResult>
}