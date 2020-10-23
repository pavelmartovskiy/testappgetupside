package com.mp.testapp.getupside.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import com.mp.testapp.getupside.domain.ArcGisRepository
import com.mp.testapp.getupside.domain.FoodPoint
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors

class ArcGisRepositoryImpl : ArcGisRepository {

    companion object {
        const val url = "https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer"
        const val maxResults = 20
    }

    override fun getFoodPoint(latitude: Double , longitude: Double): LiveData<List<FoodPoint>> {

        val liveData = MutableLiveData<List<FoodPoint>>()
        Log.v("[TEST]", "ON  RESUME")

        val lt = LocatorTask(url)

        // Get the center of the current map extent
        // Get the center of the current map extent
        val mapCenter: Point = Point(latitude, longitude)

        //Retrieve the 5 closest matches to the map center

        //Retrieve the 5 closest matches to the map center
        val geocodeParams = GeocodeParameters()
        geocodeParams.maxResults = maxResults
        geocodeParams.preferredSearchLocation = mapCenter
        geocodeParams.categories.add("food places")

        //Ensure the results return the Address, Phone and Distance

        //Ensure the results return the Address, Phone and Distance
        val resultAttributeNames = geocodeParams.resultAttributeNames
        resultAttributeNames.add("Place_addr")
        resultAttributeNames.add("Phone")
        resultAttributeNames.add("Distance")
        resultAttributeNames.add("NAme")

        //Geocode the closest ice cream shops using the specified geocoding parameters

        //Geocode the closest ice cream shops using the specified geocoding parameters
        val geocodeFuture: ListenableFuture<List<GeocodeResult>> = lt.geocodeAsync("food places", geocodeParams)

        fun GeocodeResult.map() = FoodPoint(displayLocation.x, displayLocation.y, label)

        fun List<GeocodeResult>.map() : List<FoodPoint> = stream().map { it.map() }.collect(Collectors.toList())

        geocodeFuture.addDoneListener(Runnable {

            Log.v("[TAG]", "GOT RESULt : ")

            try {
                val geocodeResults: List<GeocodeResult> = geocodeFuture.get()

                Log.v("[TAG]", "GOT RESULt : " + geocodeResults.size)

                // Use the results - for example display on the map
                for (result in geocodeResults) {
//                    addGraphic(result.displayLocation, result.attributes)
                    Log.v("[TAG]", "Result: " + result.displayLocation + " - " + result.attributes)
                }

                liveData.postValue(geocodeResults.map())

            } catch (e: InterruptedException) {
//                dealWithException(e) // deal with exception appropriately...
            } catch (e: ExecutionException) {
//                dealWithException(e)
            }
        })

        Log.v("[TAG]", "GOT WAIT ASYNC : ")

        return liveData

    }
}