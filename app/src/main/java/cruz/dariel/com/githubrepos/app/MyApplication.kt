package cruz.dariel.com.githubrepos.app

import android.app.Application
import android.content.Context
import cruz.dariel.com.githubrepos.modules.ContextModule
import cruz.dariel.com.githubrepos.modules.DatabaseModule
import cruz.dariel.com.githubrepos.modules.GithubServiceModule

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    fun getComponent(context: Context) : AppComponent = DaggerAppComponent.builder()
            .contextModule(ContextModule(context))
            .githubServiceModule(GithubServiceModule())
            .databaseModule(DatabaseModule())
            .build()

}
