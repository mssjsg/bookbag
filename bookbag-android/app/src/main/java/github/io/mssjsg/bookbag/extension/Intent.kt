@file:JvmName("IntentUtils")
@file:JvmMultifileClass

package github.io.mssjsg.bookbag.extension
import android.content.Intent

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