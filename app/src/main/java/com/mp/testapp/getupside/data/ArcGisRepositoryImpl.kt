package com.mp.testapp.getupside.data

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import com.mp.testapp.getupside.R
import com.mp.testapp.getupside.domain.*
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors


class ArcGisRepositoryImpl(activity: Activity) : ArcGisRepository {

    companion object {
        const val url = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer"
        const val maxResults = 20

        private const val ADDRESS_KEY = "Place_addr"
        private const val PHONE_KEY = "Phone"
        private const val DISTANCE_KEY = "Distance"
        private const val NAME_KEY = "PlaceName"
    }

    init {
        ArcGISRuntimeEnvironment.setLicense(activity.resources.getString(R.string.arcgis_license_key))
    }


    override fun getFoodPoint(latitude: Double, longitude: Double): LiveData<FoodPointsSearchResult> {

        val liveData = MutableLiveData<FoodPointsSearchResult>()
        val lt = LocatorTask(url)


        // Get the center of the current map extent
        // Get the center of the current map extent
        val mapCenter = Point(latitude, longitude)

        //Retrieve the 20 closest matches to the map center
        val geocodeParams = GeocodeParameters()
        geocodeParams.maxResults = maxResults
        geocodeParams.preferredSearchLocation = mapCenter
        geocodeParams.categories.add("FOOD")

        //Ensure the results return the Address, Phone, Distance, Place name

        val resultAttributeNames = geocodeParams.resultAttributeNames
        resultAttributeNames.add(ADDRESS_KEY)
        resultAttributeNames.add(PHONE_KEY)
        resultAttributeNames.add(DISTANCE_KEY)
        resultAttributeNames.add(NAME_KEY)


        //Geocode the closest ice cream shops using the specified geocoding parameters
        val geocodeFuture: ListenableFuture<List<GeocodeResult>> = lt.geocodeAsync("*", geocodeParams)

        fun GeocodeResult.map() = FoodPoint(
                displayLocation.x,
                displayLocation.y,
                attributes[NAME_KEY].toString(),
                attributes[ADDRESS_KEY].toString(),
                attributes[NAME_KEY].toString(),
                attributes[DISTANCE_KEY].toString().toDouble()
        )


        fun List<GeocodeResult>.map(): List<FoodPoint> = stream().map { it.map() }.collect(Collectors.toList())

        geocodeFuture.addDoneListener {
            try {
                val geocodeResults: List<GeocodeResult> = geocodeFuture.get()
                liveData.postValue(FoodPointsSearchSuccess(geocodeResults.map()))
            } catch (e: InterruptedException) {
                liveData.postValue(FoodPointsSearchError(e.message ?: ""))
            } catch (e: ExecutionException) {
                liveData.postValue(FoodPointsSearchError(e.message ?: ""))
            }
        }
        return liveData

    }

}