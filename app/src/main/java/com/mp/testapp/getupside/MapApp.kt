package com.mp.testapp.getupside

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration


class MapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this);
        val config = RealmConfiguration.Builder().name("foodPoints.realm").build()
        Realm.setDefaultConfiguration(config)

    }
}