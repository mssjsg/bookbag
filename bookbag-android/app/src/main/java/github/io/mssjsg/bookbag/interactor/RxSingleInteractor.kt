package github.io.mssjsg.bookbag.interactor

import io.reactivex.Single

interface RxSingleInteractor<Param, Result> {
    fun getSingle(param: Param): Single<Result>
}