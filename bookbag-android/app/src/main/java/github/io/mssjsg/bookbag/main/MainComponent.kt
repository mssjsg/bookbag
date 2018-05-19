package github.io.mssjsg.bookbag.main

import dagger.Subcomponent
import github.io.mssjsg.bookbag.ViewModelScope

@Subcomponent()
@ViewModelScope
interface MainComponent {
    fun provideMainViewModel(): MainViewModel
}