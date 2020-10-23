package com.mp.testapp.getupside.data.realm

import io.realm.RealmObject

open class RealmFoodPoint(
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var name: String = ""
) : RealmObject()