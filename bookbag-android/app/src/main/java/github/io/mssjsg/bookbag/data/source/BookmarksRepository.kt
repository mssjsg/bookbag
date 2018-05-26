package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BookmarksRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseBookmark
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class BookmarksRepository @Inject constructor(bookmarksLocalDataSource: BookmarksLocalDataSource,
                          bookmarksRemoteDataSource: BookmarksRemoteDataSource):
        BaseRepository<FirebaseBookmark, Bookmark>(bookmarksLocalDataSource, bookmarksRemoteDataSource)