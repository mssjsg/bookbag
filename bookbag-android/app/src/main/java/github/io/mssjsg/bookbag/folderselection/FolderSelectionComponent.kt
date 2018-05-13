package github.io.mssjsg.bookbag.folderselection

import dagger.Subcomponent
import github.io.mssjsg.bookbag.folderselection.FolderSelectionViewModel
import github.io.mssjsg.bookbag.main.MainViewModel
import github.io.mssjsg.bookbag.util.viewmodel.ViewModelScope

@Subcomponent()
@ViewModelScope
interface FolderSelectionComponent {
    fun provideFolderSelectionViewModel(): FolderSelectionViewModel
}