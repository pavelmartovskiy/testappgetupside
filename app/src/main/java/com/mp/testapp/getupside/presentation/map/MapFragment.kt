package com.mp.testapp.getupside.presentation.map


import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mp.testapp.getupside.R
import com.mp.testapp.getupside.domain.*
import com.mp.testapp.getupside.presentation.main.MainActivity
import com.mp.testapp.getupside.presentation.main.MainViewModel
import kotlin.math.max
import kotlin.math.min


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = (requireActivity() as MainActivity).getViewModel()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.f_map, container, false)

    override fun onMapReady(googleMap: GoogleMap) {

        val markers = mutableListOf<Marker>()

        fun addPoints(points: List<FoodPoint>) {
            markers.forEach { marker -> marker.remove() }
            markers.clear()
            points.forEach { point ->
                val markerOptions = MarkerOptions()
                        .position(LatLng(point.latitude, point.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .title(point.name)
                val marker = googleMap.addMarker(markerOptions)
                markers.add(marker)
            }
        }



        viewModel.followVisibleFoodPoints().observe(this, { result ->
            when (result) {
                is FoodPointsSearchSuccess -> addPoints(result.points)
                is FoodPointsSearchError -> Snackbar.make(requireView(), result.message, BaseTransientBottomBar.LENGTH_LONG).show()
            }
        })

        viewModel.followUserLocation().observe(this, { locationResult -> processLocationResult(locationResult, googleMap) })

        val gmcsl = GoogleMapCameraStopMovedListener {
            val bounds = googleMap.projection.visibleRegion.latLngBounds

            viewModel.requestFoodPoints(
                    min(bounds.southwest.longitude, bounds.northeast.longitude),
                    min(bounds.southwest.latitude, bounds.northeast.latitude),
                    max(bounds.southwest.longitude, bounds.northeast.longitude),
                    max(bounds.southwest.latitude, bounds.northeast.latitude)
            )

        }

        googleMap.setOnCameraIdleListener(gmcsl)
        googleMap.setOnCameraMoveListener(gmcsl)

    }

    private class GoogleMapCameraStopMovedListener(
            private val run: Runnable
    ) : GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

        var moving: Boolean = false

        override fun onCameraIdle() {
            if (moving) {
                run.run()
                moving = false
            }
        }

        override fun onCameraMove() {
            moving = true
        }

    }

    private fun processLocationResult(locationResult: CurrentLocationResult?, googleMap: GoogleMap) {
        when (locationResult) {
            is PermissionsRequest -> requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_ACCESS_FINE_LOCATION_PERMISSION)
            is CurrentLocation -> moveCamera(googleMap, locationResult)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode != REQUEST_ACCESS_FINE_LOCATION_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size != 1) {
            throw IllegalStateException("Illegal number permission for user location: " + grantResults.size + ". Should be: 1")
        }

        if (grantResults[0] == PERMISSION_GRANTED) {
            viewModel.requestUserLocation()
        }

    }

    private fun moveCamera(map: GoogleMap, location: CurrentLocation) {
        map.addMarker(MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Your Current Location"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
        map.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
    }

    companion object {
        const val REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 0x1001
    }
}