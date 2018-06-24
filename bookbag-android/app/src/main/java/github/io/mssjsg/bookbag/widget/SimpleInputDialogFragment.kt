package github.io.mssjsg.bookbag.widget

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.widget.EditText
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.util.extension.getAppComponent
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LiveEvent

class SimpleInputDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_REQUEST_ID = "github.io.mssjsg.bookbag.widget.ARG_REQUEST_ID"
        private const val ARG_HINT = "github.io.mssjsg.bookbag.widget.ARG_HINT"
        private const val ARG_TITLE = "github.io.mssjsg.bookbag.widget.ARG_TITLE"

        fun newInstance(requestId: String, hint: String, title: String): SimpleInputDialogFragment {
            return SimpleInputDialogFragment().apply {
                setArguments(Bundle().apply {
                    putString(ARG_REQUEST_ID, requestId)
                    putString(ARG_HINT, hint)
                    putString(ARG_TITLE, title)
                })
            }
        }
    }

    private lateinit var hint: String
    private lateinit var title: String
    private lateinit var requestId: String

    var listener: Listener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            hint = getString(ARG_HINT) ?: ""
            requestId = getString(ARG_REQUEST_ID) ?: ""
            title = getString(ARG_TITLE) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_simple_input, null)
        val editText = view.findViewById<EditText>(R.id.edit_input)
        view.findViewById<TextInputLayout>(R.id.input_layout).hint = hint
        val builder = AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.dialog_ok, {
                    dialogInterface, i -> listener?.onConfirm(this, requestId, editText.text.toString())
                })
                .setNegativeButton(R.string.dialog_cancel, {
                    dialogInterface, i -> listener?.onCancel(this, requestId)
                })
        isCancelable = false
        return builder.create()
    }

    interface Listener {
        fun onConfirm(simpleInputDialogFragment: SimpleInputDialogFragment, requestId: String, input: String)
        fun onCancel(simpleInputDialogFragment: SimpleInputDialogFragment, requestId: String)
    }
}