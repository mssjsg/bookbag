package github.io.mssjsg.bookbag

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by Sing on 27/3/2018.
 */
@Module
class AppModule(val application: Application) {

    @Provides
    fun providesApplicationContext(): Context {
        return application
    }

    @Provides
    fun providesApplication(): Application {
        return application
    }
}