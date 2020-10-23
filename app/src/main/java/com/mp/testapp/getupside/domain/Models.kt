package com.mp.testapp.getupside.domain

sealed class FoodPointsSearchResult

data class FoodPointsSearchError(
        val message: String
) : FoodPointsSearchResult()

data class FoodPointsSearchSuccess(
        val points: List<FoodPoint>
) : FoodPointsSearchResult()

data class FoodPoint(
        val latitude: Double,
        val longitude: Double,
        val name: String,
        val address: String,
        val phone: String,
        val distance: Double
)

sealed class CurrentLocationResult

data class PermissionsRequest(
        val missingACCESS_FINE_LOCATION: Boolean = true,
) : CurrentLocationResult()

data class CurrentLocation(
        val latitude: Double,
        val longitude: Double
) : CurrentLocationResult()