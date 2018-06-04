package github.io.mssjsg.bookbag.intro

import dagger.Subcomponent
import github.io.mssjsg.bookbag.ViewModelScope

@Subcomponent
@ViewModelScope
interface IntroComponent {
    fun inject(introViewModel: IntroViewModel)
    fun provideViewModel(): IntroViewModel
}