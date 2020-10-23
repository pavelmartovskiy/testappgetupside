package com.mp.testapp.getupside.data.realm

import androidx.lifecycle.*
import com.mp.testapp.getupside.domain.FoodPoint
import com.mp.testapp.getupside.domain.RealmRepository
import io.realm.Realm
import java.util.concurrent.Executors
import java.util.stream.Collectors

class RealmRepositoryImpl() : RealmRepository, LifecycleObserver {

    private val realm: Realm = Realm.getDefaultInstance()
    private val executor = Executors.newFixedThreadPool(1) { runnable ->
        val thread = Thread(runnable)
        thread
    }

    init {
    }

    override fun createOrUpdateAll(points: List<FoodPoint>) {
        realm.executeTransactionAsync { r -> r.insert(points.map { point -> point.mapToRealm() }) }
    }

    override fun getAllPoints(): LiveData<List<FoodPoint>> {

        val liveData = MutableLiveData<List<FoodPoint>>()

        val findAll = realm.where(RealmFoodPoint::class.java).findAllAsync()

        findAll.addChangeListener { source ->
            val collect = source
                    .stream()
                    .map { it.mapToDomain() }
                    .collect(Collectors.toList())
            liveData.postValue(collect)
        }

        return liveData
    }

    override fun deleteAll() {
        Realm.getDefaultInstance().executeTransactionAsync { r ->
            r.deleteAll()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        executor.shutdown()
        realm.close()
    }

    private fun FoodPoint.mapToRealm() = RealmFoodPoint(latitude, longitude, name, address, phone, distance)
    private fun RealmFoodPoint.mapToDomain() = FoodPoint(latitude, longitude, name, address, phone, distance)

}