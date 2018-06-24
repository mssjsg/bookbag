package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import io.reactivex.Single
import javax.inject.Inject

class GetBookmarkInteractor @Inject constructor(private val bookmarksRepository: BookmarksRepository): RxSingleInteractor<String, Bookmark> {
    override fun getSingle(param: String): Single<Bookmark> {
        return bookmarksRepository.getItem(param)
    }
}