package github.io.mssjsg.bookbag.user

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserModule {
    @Singleton
    @Provides
    fun provideUserData(): MutableLiveData<FirebaseUser> {
        return MutableLiveData()
    }
}