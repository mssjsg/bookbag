package github.io.mssjsg.bookbag.interactor

import io.reactivex.Observable

interface RxObservableInteractor<T> {
    fun getObservable(): Observable<T>
}