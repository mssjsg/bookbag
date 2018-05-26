package github.io.mssjsg.bookbag.interactor

import io.reactivex.Flowable

interface RxFlowableInteractor<Param, Result> {
    fun getFlowable(param: Param): Flowable<Result>
}