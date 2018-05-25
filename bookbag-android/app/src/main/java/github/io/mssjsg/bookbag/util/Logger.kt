package github.io.mssjsg.bookbag.util

import android.util.Log
import javax.inject.Inject

class Logger @Inject constructor() {

    fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    fun e(tag: String, message: String? = null, e: Throwable? = null) {
        Log.e(tag, message, e)
    }
}
