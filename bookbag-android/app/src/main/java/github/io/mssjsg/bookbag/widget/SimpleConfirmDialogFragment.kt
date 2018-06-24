package github.io.mssjsg.bookbag.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.util.extension.getAppComponent
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

    var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            requestId = getString(ARG_REQUEST_ID) ?: ""
            title = getString(ARG_TITLE) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.dialog_ok, {
                    dialogInterface, i -> listener?.onConfirm(this, requestId)
                })
                .setNegativeButton(R.string.dialog_cancel, {
                    dialogInterface, i -> listener?.onCancel(this, requestId)
                })
        isCancelable = false
        return builder.create()
    }

    interface Listener {
        fun onConfirm(simpleConfirmDialogFragment: SimpleConfirmDialogFragment, requestId: String)
        fun onCancel(simpleConfirmDialogFragment: SimpleConfirmDialogFragment, requestId: String)
    }
}