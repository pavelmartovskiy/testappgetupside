package com.mp.testapp.getupside.data.realm

import androidx.lifecycle.*
import com.mp.testapp.getupside.domain.FoodPoint
import com.mp.testapp.getupside.domain.RealmRepository
import io.realm.Realm
import java.util.concurrent.Executors
import java.util.stream.Collectors

class RealmRepositoryImpl() : RealmRepository, LifecycleObserver {

    private val realm: Realm = Realm.getDefaultInstance()
    private val executor = Executors.newSingleThreadExecutor();


    override fun createOrUpdateAll(points: List<FoodPoint>) {
        executor.run {
            realm.executeTransaction { r -> r.insert(points.map { point -> point.mapToRealm() }) }
        }
    }

    override fun getAllPoints(): LiveData<List<FoodPoint>> = Transformations.map(
            RealmLiveData<RealmFoodPoint>(realm.use { r -> r.where(RealmFoodPoint::class.java).findAll() })
    ) { source ->
        source
                .stream()
                .map { it.mapToDomain() }
                .collect(Collectors.toList())
    }

    override fun deleteAll() {
        executor.run { realm.executeTransaction { r -> r.deleteAll() } }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
        executor.shutdown()
        realm.close()
    }

    private fun FoodPoint.mapToRealm() = RealmFoodPoint(latitude, longitude, name)
    private fun RealmFoodPoint.mapToDomain() = FoodPoint(latitude, longitude,  name ?: "")

}