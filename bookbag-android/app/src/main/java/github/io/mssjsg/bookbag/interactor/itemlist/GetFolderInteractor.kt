package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxFlowableInteractor
import io.reactivex.Flowable
import javax.inject.Inject

class GetFolderInteractor @Inject constructor(val foldersRepository: FoldersRepository): RxFlowableInteractor<String?, GetFolderInteractor.Result> {
    override fun getFlowable(param: String?): Flowable<Result> {
        return param?.let {
            foldersRepository.getItem(it).map { Result(it) }
        } ?: kotlin.run {
            Flowable.just(Result(null))
        }
    }

    class Result(val folder: Folder?)
}