package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import github.io.mssjsg.bookbag.data.source.local.FoldersLocalDataSource
import github.io.mssjsg.bookbag.data.source.remote.FoldersRemoteDataSource
import github.io.mssjsg.bookbag.data.source.remote.data.FirebaseFolder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Sing on 27/3/2018.
 */
@Singleton
class FoldersRepository @Inject constructor(foldersLocalDataSource: FoldersLocalDataSource,
                                            foldersRemoteDataSource: FoldersRemoteDataSource): BaseRepository<FirebaseFolder, Folder>(foldersLocalDataSource, foldersRemoteDataSource)
