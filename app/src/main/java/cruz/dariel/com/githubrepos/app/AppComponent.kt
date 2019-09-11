package cruz.dariel.com.githubrepos.app

import cruz.dariel.com.githubrepos.modules.*
import dagger.Component

@Component(modules = [ApiModule::class, RealmModule::class, GithubServiceModule::class])
interface AppComponent{


}