package github.io.mssjsg.bookbag.data.source

import github.io.mssjsg.bookbag.data.Folder
import io.reactivex.Flowable

/**
 * Created by Sing on 27/3/2018.
 */
interface FoldersDataSource {
    fun getFolders(folderId: String? = null) : Flowable<List<Folder>>

    fun getCurrentFolder(folderId: String) : Flowable<Folder>

    fun getDirtyFolders(): Flowable<List<Folder>>

    fun saveFolder(folder: Folder)

    fun updateFolder(folder: Folder)

    fun moveFolder(folderId: String, parentFolderId: String?)

    fun deleteFolders(folderIds: List<String>)
}