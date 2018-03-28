package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.BookBagAppComponent
import github.io.mssjsg.bookbag.R
import github.io.mssjsg.bookbag.data.Bookmark
import github.io.mssjsg.bookbag.databinding.ActivityMainBinding
import github.io.mssjsg.bookbag.util.getSharedUrl
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var mainBinding : ActivityMainBinding
    private lateinit var mainListAdapter : MainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProviders.of(this, ViewModelFactory(getAppComponent()))
                .get(MainViewModel::class.java)
        viewModel.observe(this)

        mainBinding.viewmodel = viewModel
        mainListAdapter = MainListAdapter(viewModel)
        mainBinding.bookmarksList.layoutManager = LinearLayoutManager(this)
        mainBinding.bookmarksList.adapter = mainListAdapter
        mainBinding.bookmarksList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        detectNewUrl(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        detectNewUrl(intent)
    }

    private fun getAppComponent(): BookBagAppComponent {
        return (application as BookBagApplication).appComponent
    }

    private fun detectNewUrl(intent: Intent?) {
        val newUrl = intent?.getSharedUrl()
        if (!newUrl.isNullOrBlank()) {
            viewModel.addBookmark(Bookmark(url = newUrl?:""))
        }
    }
}
