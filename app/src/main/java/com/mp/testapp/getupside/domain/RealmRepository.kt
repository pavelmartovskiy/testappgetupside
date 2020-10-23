package com.mp.testapp.getupside.domain

import androidx.lifecycle.LiveData

interface RealmRepository {
    fun createOrUpdateAll(points: List<FoodPoint>)
    fun getAllPoints(): LiveData<List<FoodPoint>>
    fun deleteAll();
}