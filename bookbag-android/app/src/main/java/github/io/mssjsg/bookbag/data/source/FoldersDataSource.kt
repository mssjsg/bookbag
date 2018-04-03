package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
interface FoldersDataSource {
    fun getFolders(folderId: String? = null) : Flowable<List<Folder>>

    fun saveFolder(folder: Folder)

    fun updateFolder(folder: Folder)

    fun deleteFolders(folderIds: List<Int>)
}