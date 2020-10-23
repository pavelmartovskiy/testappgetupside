package com.mp.testapp.getupside.presentation.list

data class FoodPointItem(
        val latitude : Double,
        val longitude : Double,
        val name: String,
        val address: String,
        val phone: String,
        val distance: String
) {
    val position: String = "$latitude : $longitude"
}