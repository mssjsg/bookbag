package github.io.mssjsg.bookbag.util

import android.app.Application
import android.support.annotation.StringRes
import javax.inject.Inject

class StringProvider @Inject constructor(val application: Application) {

    fun getString(@StringRes id: Int): String {
        return application.getString(id)
    }
}