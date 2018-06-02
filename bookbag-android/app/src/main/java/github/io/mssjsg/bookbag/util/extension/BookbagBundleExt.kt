@file:JvmName("IntentUtils")
@file:JvmMultifileClass

package github.io.mssjsg.bookbag.util.extension
import android.os.Bundle

const val ARG_FOLDER_ID = "github.io.mssjsg.bookbag.ARG_FOLDER_ID"
const val ARG_FILTERED_FOLDER_IDS = "github.io.mssjsg.bookbag.ARG_FILTERED_FOLDER_IDS"

fun Bundle.putFolderId(folderId: String?) {
    putString(ARG_FOLDER_ID, folderId)
}

fun Bundle.getFolderId(): String? {
    return getString(ARG_FOLDER_ID)
}

fun Bundle.putFilteredFolderIds(filteredFolderIds: Array<String>) {
    putStringArray(ARG_FILTERED_FOLDER_IDS, filteredFolderIds)
}

fun Bundle.getFilteredFolderIds(): Array<String> {
    return getStringArray(ARG_FILTERED_FOLDER_IDS) ?: emptyArray()
}