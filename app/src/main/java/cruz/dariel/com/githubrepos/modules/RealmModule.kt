package cruz.dariel.com.githubrepos.modules

import android.content.Context
import dagger.Module
import io.realm.Realm
import io.realm.RealmConfiguration

@Module(includes = [ContextModule::class ])
class RealmModule{

    fun provideRealm(context: Context) : Realm{
        Realm.init(context) // should only be done once when app starts

        val config = RealmConfiguration.Builder()
            .name("github.realm")
            .build()

        return Realm.getInstance(config)
    }

}