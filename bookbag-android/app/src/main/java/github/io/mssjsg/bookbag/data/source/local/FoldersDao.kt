package github.io.mssjsg.bookbag.data.source.local

import android.arch.persistence.room.*
import github.io.mssjsg.bookbag.data.Folder
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Sing on 27/3/2018.
 */
@Dao
interface FoldersDao {

    @Query("SELECT * FROM folders WHERE dirty IS 1")
    fun getDirtyFolders(): Flowable<List<Folder>>

    @Query("SELECT * FROM folders WHERE parent_folder_id IS NULL")
    fun getHomeFolders(): Flowable<List<Folder>>

    @Query("SELECT * FROM folders WHERE parent_folder_id = :folderId")
    fun getFoldersByParentFolderId(folderId: String): Flowable<List<Folder>>

    @Query("SELECT * FROM folders WHERE folder_id = :folderId")
    fun getFolder(folderId: String): Single<Folder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFolder(folder: Folder)

    @Update
    fun updateFolder(folder: Folder): Int

    @Query("DELETE FROM folders WHERE folder_id = :folderId")
    fun deleteFolderByFolderId(folderId: String)

    @Query("UPDATE folders SET parent_folder_id = :parentFolderId AND dirty = 1 WHERE folder_id = :folderId")
    fun moveFolder(folderId: String, parentFolderId: String?)
}