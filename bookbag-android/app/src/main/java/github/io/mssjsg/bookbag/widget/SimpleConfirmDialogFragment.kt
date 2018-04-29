package github.io.mssjsg.bookbag.widget

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.EditText
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.util.getAppComponent
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LiveEvent

class SimpleConfirmDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_REQUEST_ID = "github.io.mssjsg.bookbag.widget.ARG_REQUEST_ID"
        private const val ARG_TITLE = "github.io.mssjsg.bookbag.widget.ARG_TITLE"

        fun newInstance(requestId: String, title: String): SimpleConfirmDialogFragment {
            return SimpleConfirmDialogFragment().apply {
                setArguments(Bundle().apply {
                    putString(ARG_REQUEST_ID, requestId)
                    putString(ARG_TITLE, title)
                })
            }
        }
    }

    private lateinit var title: String
    private lateinit var requestId: String

    private lateinit var liveBus: LiveBus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            requestId = getString(ARG_REQUEST_ID) ?: ""
            title = getString(ARG_TITLE) ?: ""
        }

        liveBus = getAppComponent().provideLiveBus()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.dialog_ok, {
                    dialogInterface, i -> liveBus.post(ConfirmEvent(requestId))
                })
                .setNegativeButton(R.string.dialog_cancel, {
                    dialogInterface, i -> liveBus.post(CancelEvent(requestId))
                })
                .create()
    }

    data class ConfirmEvent(val requestId: String): LiveEvent()

    data class CancelEvent(val requestId: String): LiveEvent()
}