package github.io.mssjsg.bookbag.util.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Created by Sing on 27/3/2018.
 */
class MainThreadExecutor: Executor {
    private val handler: Handler = Handler(Looper.getMainLooper())

    override fun execute(runnable: Runnable?) {
        runnable?.let { handler.post(runnable) }
    }
}