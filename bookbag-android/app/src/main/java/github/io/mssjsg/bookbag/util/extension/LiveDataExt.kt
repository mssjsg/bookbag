package github.io.mssjsg.bookbag.util.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                observer(t)
            }
        }
    })
}

fun <T> LiveData<T>.observeNullable(owner: LifecycleOwner, observer: (T?) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer(t)
        }
    })
}

fun <T> LiveData<T>.observeForeverNonNull(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            t?.let {
                observer(t)
            }
        }
    })
}
