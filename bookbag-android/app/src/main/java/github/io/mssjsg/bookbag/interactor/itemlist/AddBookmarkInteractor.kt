package github.io.mssjsg.bookbag.interactor.itemlist

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.interactor.RxSingleInteractor
import github.io.mssjsg.bookbag.util.linkpreview.JsoupWebPageCrawler
import github.io.mssjsg.bookbag.util.linkpreview.SearchUrls
import io.reactivex.Single
import javax.inject.Inject

class AddBookmarkInteractor @Inject constructor(val bookmarksRepository: BookmarksRepository): RxSingleInteractor<AddBookmarkInteractor.Param, String> {
    override fun getSingle(param: Param): Single<String> {
        return Single.fromCallable<String>({
            val url = param.url
            val urls = SearchUrls.matches(url)
            if (urls.size > 0) {
                JsoupWebPageCrawler.extendedTrim(urls.get(0))
            } else {
                null
            }
        }).flatMap { detectedUrl ->
            if (detectedUrl.isNotEmpty()) {
                bookmarksRepository.saveItem(Bookmark(url = detectedUrl,
                        folderId = param.folderId))
            } else {
                Single.just(null)
            }
        }
    }

    class Param(val url: String, val folderId: String?)
}