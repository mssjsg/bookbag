package github.io.mssjsg.bookbag

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import github.io.mssjsg.bookbag.data.BookmarkItem
import github.io.mssjsg.bookbag.databinding.ActivityMainBinding
import github.io.mssjsg.bookbag.extension.getSharedUrl

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BookmarksViewModel
    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var mainListAdapter : MainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = BookmarksViewModel(application)
        mainBinding.viewmodel = viewModel
        mainListAdapter = MainListAdapter()
        mainBinding.bookmarksList.layoutManager = LinearLayoutManager(this)
        mainBinding.bookmarksList.adapter = mainListAdapter

        detectNewUrl(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        detectNewUrl(intent)
    }

    private fun detectNewUrl(intent: Intent?) {
        val newUrl = intent?.getSharedUrl()
        if (!newUrl.isNullOrBlank()) {
            viewModel.items.add(BookmarkItem(id = 0, name = "bookmark1", url = newUrl?:""))
        }
    }
}
