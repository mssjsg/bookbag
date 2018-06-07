package github.io.mssjsg.bookbag.interactor

import io.reactivex.Completable

interface RxCompletableInteractor<Param> {
    fun getCompletable(param: Param): Completable
}