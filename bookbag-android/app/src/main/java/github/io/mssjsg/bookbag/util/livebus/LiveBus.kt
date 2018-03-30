package github.io.mssjsg.bookbag.util.livebus

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

/**
 * Created by Sing on 30/3/2018.
 */
@Singleton
class LiveBus @Inject constructor() {

    private val bus: MutableLiveData<LiveEvent> = MutableLiveData<LiveEvent>()

    fun <T: LiveEvent> subscribe(owner: LifecycleOwner, observer: Observer<T>, kClass: KClass<T>) {
        bus.observe(owner, Observer {
            if (kClass.isInstance(it)) observer.onChanged(kClass.cast(it))
        })
    }

    fun post(liveEvent: LiveEvent) {
        bus.postValue(liveEvent)
    }
}