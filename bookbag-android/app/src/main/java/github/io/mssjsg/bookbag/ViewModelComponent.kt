package github.io.mssjsg.bookbag

import dagger.Subcomponent
import github.io.mssjsg.bookbag.folderselection.FolderSelectionViewModel
import github.io.mssjsg.bookbag.main.MainViewModel
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope

@Subcomponent()
@ViewModelScope
interface ViewModelComponent {
    fun provideMainViewModel(): MainViewModel

    fun provideFolderSelectionViewModel(): FolderSelectionViewModel
}