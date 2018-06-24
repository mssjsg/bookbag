package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxFlowableInteractor
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class GetFolderInteractor @Inject constructor(val foldersRepository: FoldersRepository): RxSingleInteractor<String?, GetFolderInteractor.Result> {
    override fun getSingle(param: String?): Single<Result> {
        return param?.let {
            foldersRepository.getItem(it).map { Result(it) }
        } ?: kotlin.run {
            Single.just(Result(null))
        }
    }

    class Result(val folder: Folder?)
}