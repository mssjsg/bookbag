package github.io.mssjsg.bookbag.util

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import javax.inject.Inject

class RxTransformers @Inject constructor(val schedulers: RxSchedulers) {

    fun <T> applySchedulersOnObservable(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.subscribeOn(schedulers.io()).observeOn(schedulers.mainThread())
        }
    }

    fun <T> applySchedulersOnFlowable(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            it.subscribeOn(schedulers.io()).observeOn(schedulers.mainThread())
        }
    }

    fun <T> applySchedulersOnSingle(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.subscribeOn(schedulers.io()).observeOn(schedulers.mainThread())
        }
    }

}