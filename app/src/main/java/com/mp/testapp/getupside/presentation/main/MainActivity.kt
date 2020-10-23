package com.mp.testapp.getupside.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.mp.testapp.getupside.R
import com.mp.testapp.getupside.data.ArcGisRepositoryImpl
import com.mp.testapp.getupside.data.LocationRepositoryImpl
import com.mp.testapp.getupside.data.realm.RealmRepositoryImpl

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)
        val vp = findViewById<ViewPager>(R.id.vp)
        vp.adapter = MainPageAdapter(this, supportFragmentManager)
        viewModel = MainViewModel(
                ArcGisRepositoryImpl(this),
                LocationRepositoryImpl(this),
                RealmRepositoryImpl()
        )
    }

    fun getViewModel() = viewModel
}