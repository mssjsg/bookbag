package github.io.mssjsg.bookbag.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BookbagSchedulers @Inject constructor() {
    fun io(): Scheduler {
        return Schedulers.io()
    }

    fun mainThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}