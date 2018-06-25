package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.data.source.local.BookmarksLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.BookmarksRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseBookmark

class DefaultBookmarksRepository(bookmarksLocalDataSource: BookmarksLocalDataSource,
                                              bookmarksRemoteDataSource: BookmarksRemoteDataSource):
        BaseRepository<FirebaseBookmark, Bookmark>(bookmarksLocalDataSource, bookmarksRemoteDataSource), BookmarksRepository
