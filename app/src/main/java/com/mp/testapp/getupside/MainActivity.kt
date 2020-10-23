package com.mp.testapp.getupside

//import io.realm.Realm

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mp.testapp.getupside.data.ArcGisRepositoryImpl
import com.mp.testapp.getupside.data.LocationRepositoryImpl
import com.mp.testapp.getupside.data.realm.RealmRepositoryImpl
import com.mp.testapp.getupside.presentation.MapViewModel


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    private lateinit var viewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = MapViewModel(
                ArcGisRepositoryImpl(),
                LocationRepositoryImpl(),
                RealmRepositoryImpl()
        )


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        viewModel.getFoodPoint().observe(this, Observer { points ->
            points.forEach { point ->
                val latLan = LatLng(point.latitude, point.longitude)
                mMap.addMarker(MarkerOptions()
                        .position(latLan)
                        .title(point.name ?: ""))
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLan))
            }
        })


        viewModel.getUserLocation().observe(this, {
            mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)).title("Your Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 2000, null)
        })
    }
}