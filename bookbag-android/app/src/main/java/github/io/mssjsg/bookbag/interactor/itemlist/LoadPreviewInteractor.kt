package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import github.io.mssjsg.bookbag.util.linkpreview.UrlPreviewManager
import io.reactivex.Single
import javax.inject.Inject

class LoadPreviewInteractor @Inject constructor(val urlPreviewManager: UrlPreviewManager,
                                                val bookmarksRepository: BookmarksRepository): RxSingleInteractor<String, Bookmark> {
    override fun getSingle(param: String): Single<Bookmark> {
        return Single.fromCallable({
            urlPreviewManager.get(param)
        }).flatMap { item ->
            val previewUrl = item.previewUrl
            val title = item.title
            bookmarksRepository.getItem(param).flatMap { bookmark ->
                        bookmarksRepository.updateItem(bookmark.copy(imageUrl = previewUrl, name = title, dirty = true))
                    }.flatMap {
                        bookmarksRepository.getItem(it)
                    }
        }
    }
}