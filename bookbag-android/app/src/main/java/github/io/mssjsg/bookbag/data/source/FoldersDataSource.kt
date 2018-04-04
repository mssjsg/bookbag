package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
interface FoldersDataSource {
    fun getFolders(folderId: Int? = null) : Flowable<List<Folder>>

    fun getCurrentFolder(folderId: Int) : Flowable<Folder>

    fun saveFolder(folder: Folder)

    fun updateFolder(folder: Folder)

    fun deleteFolders(folderIds: List<Int>)
}