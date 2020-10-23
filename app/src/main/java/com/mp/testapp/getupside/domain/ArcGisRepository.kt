package com.mp.testapp.getupside.domain

import androidx.lifecycle.LiveData

interface ArcGisRepository {
    fun getFoodPoint(latitude: Double , longitude: Double) : LiveData<FoodPointsSearchResult>
}