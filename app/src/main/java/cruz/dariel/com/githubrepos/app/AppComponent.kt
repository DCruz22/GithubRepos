package cruz.dariel.com.githubrepos.app

import cruz.dariel.com.githubrepos.activities.DisplayActivity
import cruz.dariel.com.githubrepos.modules.*
import dagger.Component

@Component(modules = [DatabaseModule::class, GithubServiceModule::class, ContextModule::class])
interface AppComponent{
    fun inject(displayAct: DisplayActivity)
}