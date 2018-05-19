package github.io.mssjsg.bookbag.main

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import github.io.mssjsg.bookbag.BookBagApplication
import github.io.mssjsg.bookbag.data.source.BookmarksRepository
import github.io.mssjsg.bookbag.data.source.FoldersRepository
import github.io.mssjsg.bookbag.list.ItemListViewModel
import github.io.mssjsg.bookbag.util.linkpreview.UrlPreviewManager
import github.io.mssjsg.bookbag.util.livebus.LiveBus
import github.io.mssjsg.bookbag.util.livebus.LocalLiveBus
import javax.inject.Inject

class MainViewModel @Inject constructor(application: BookBagApplication,
                                        bookmarksRepository: BookmarksRepository,
                                        foldersRepository: FoldersRepository,
                                        liveBus: LiveBus,
                                        localLiveBus: LocalLiveBus,
                                        firebaseUserData: MutableLiveData<FirebaseUser>,
                                        urlPreviewManager: UrlPreviewManager) : ItemListViewModel(application,
        bookmarksRepository, foldersRepository, liveBus,  localLiveBus, firebaseUserData, urlPreviewManager) {
    lateinit var mainComponent: MainComponent
}