@file:JvmName("IntentUtils")
@file:JvmMultifileClass

package github.io.mssjsg.bookbag.util.extension
import android.content.Intent

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

fun Intent.putFolderId(folderId: String?) {
    putExtra(INTENT_EXTRA_FOLDER_ID, folderId)
}

fun Intent.getFolderId(): String? {
    return getStringExtra(INTENT_EXTRA_FOLDER_ID)
}

fun Intent.putFilteredFolderIds(filteredFolderIds: Array<String>) {
    putExtra(INTENT_EXTRA_FILTERED_FOLDER_IDS, filteredFolderIds)
}

fun Intent.getFilteredFolderIds(): Array<String> {
    return getStringArrayExtra(INTENT_EXTRA_FILTERED_FOLDER_IDS) ?: emptyArray()
}