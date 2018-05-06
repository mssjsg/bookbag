package github.io.mssjsg.bookbag.util

import dagger.Binds
import dagger.Module
import github.io.mssjsg.bookbag.util.linkpreview.JsoupWebPageCrawler
import github.io.mssjsg.bookbag.util.linkpreview.WebPageCrawler

/**
 * Created by Sing on 30/3/2018.
 */
@Module
interface UtilModule {
    @Binds
    fun bindWebPageCrawler(jsoupWebPageCrawler: JsoupWebPageCrawler): WebPageCrawler
}