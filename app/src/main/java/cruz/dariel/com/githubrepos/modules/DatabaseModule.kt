package cruz.dariel.com.githubrepos.modules

import android.content.Context
import cruz.dariel.com.githubrepos.models.repos.RepositoryRepo
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration

@Module(includes = [RealmModule::class])
class DatabaseModule{

    @Provides
    fun provideDbRepo(realm: Realm) : RepositoryRepo{
        return RepositoryRepo(realm)
    }

}