@file:JvmName("IntentUtils")
@file:JvmMultifileClass

package github.io.mssjsg.bookbag.util
import android.content.Intent
import github.io.mssjsg.bookbag.list.ItemListActivity

const val INTENT_FOLDER_ID_ROOT = -1
const val INTENT_EXTRA_FOLDER_ID = "github.io.mssjsg.bookbag.list.EXTRA_FOLDER_ID"
const val INTENT_EXTRA_FILTERED_FOLDER_IDS = "github.io.mssjsg.bookbag.list.INTENT_EXTRA_FILTERED_FOLDER_IDS"

/**
 * Created by Sing on 26/3/2018.
 */
fun Intent.getSharedUrl() : String {
    if (extras != null) {
        val shareVia = extras.getString(Intent.EXTRA_TEXT)
        if (shareVia != null) {
            return shareVia
        }
    }
    if (action == Intent.ACTION_VIEW) {
        val data = getData()
        val scheme = data.getScheme()
        val host = data.getHost()
        val params = data.getPathSegments()
        var builded = scheme + "://" + host + "/"

        for (string in params) {
            builded += string + "/"
        }

        if (data.getQuery() != null && data.getQuery() != "") {
            builded = builded.substring(0, builded.length - 1)
            builded += "?" + data.getQuery()
        }

        return builded
    }

    return ""
}

fun Intent.putFolderId(folderId: Int?) {
    putExtra(INTENT_EXTRA_FOLDER_ID, folderId ?: INTENT_FOLDER_ID_ROOT)
}

fun Intent.getFolderId(): Int? {
    val folderId = getIntExtra(INTENT_EXTRA_FOLDER_ID, INTENT_FOLDER_ID_ROOT)
    return if (folderId == INTENT_FOLDER_ID_ROOT) null else folderId
}

fun Intent.putFilteredFolderIds(filteredFolderIds: IntArray) {
    putExtra(INTENT_EXTRA_FILTERED_FOLDER_IDS, filteredFolderIds)
}

fun Intent.getFilteredFolderIds(): IntArray {
    return getIntArrayExtra(INTENT_EXTRA_FILTERED_FOLDER_IDS) ?: IntArray(0)
}