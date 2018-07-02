package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.interactor.RxCompletableInteractor
import io.reactivex.Completable
import javax.inject.Inject

class ClearDatabaseInteractor @Inject constructor(private val bookmarksRepository: BookmarksRepository,
                                                  private val foldersRepository: FoldersRepository): RxCompletableInteractor<Void?> {
    override fun getCompletable(param: Void?): Completable {
        return Completable.merge(listOf(bookmarksRepository.deleteAllItems(), foldersRepository.deleteAllItems()))
    }
}