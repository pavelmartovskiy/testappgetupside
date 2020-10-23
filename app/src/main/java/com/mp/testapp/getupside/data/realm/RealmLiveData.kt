package com.mp.testapp.getupside.data.realm

import androidx.lifecycle.MutableLiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults


class RealmLiveData<T : RealmObject>(
        private val realmResults: RealmResults<T>
) : MutableLiveData<RealmResults<T>>(), RealmChangeListener<RealmResults<T>> {

    override fun onActive() {
        super.onActive()
        realmResults.addChangeListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        realmResults.removeChangeListener(this)
    }

    override fun onChange(results: RealmResults<T>) {
        postValue(results)
    }
}