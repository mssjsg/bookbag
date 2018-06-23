package github.io.mssjsg.bookbag.util.livebus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import github.io.mssjsg.bookbag.util.extension.observeNonNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * Created by Sing on 30/3/2018.
 */
@Singleton
open class LiveBus @Inject constructor() {

    private val bus: MutableLiveData<LiveEvent> = EventLiveData()

    fun <T: LiveEvent> subscribe(owner: LifecycleOwner, observer: (T) -> Unit, kClass: KClass<T>) {
        bus.observeNonNull(owner, {
            if (kClass.isInstance(it)) {
                observer(kClass.cast(it))
            }
        })
    }

    fun post(liveEvent: LiveEvent) {
        bus.postValue(liveEvent)
    }

    companion object {
        private class EventLiveData: MutableLiveData<LiveEvent>() {
            override fun onInactive() {
                super.onInactive()
                value = null
            }
        }
    }
}