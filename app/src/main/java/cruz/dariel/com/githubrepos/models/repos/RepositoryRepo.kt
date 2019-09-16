package cruz.dariel.com.githubrepos.models.repos

import android.content.Context
import cruz.dariel.com.githubrepos.extensions.toast
import cruz.dariel.com.githubrepos.models.Repository
import io.realm.Realm

class RepositoryRepo(private val realm: Realm) {

    fun findById(id: Int): Repository? {
        return realm.where(Repository::class.java).equalTo("id", id).findFirst()
    }

    fun findAll(): List<Repository> {
        return realm.where(Repository::class.java).findAll()
    }

    fun addRepo(item: Repository?) {
        item?.let {
            realm.executeTransactionAsync ( {
                realm -> realm.copyToRealmOrUpdate(item)
            }, {
                }, {
                }
            )
        }
    }

    fun deleteRepoById(id: Int) {
        realm.beginTransaction()
        val result = realm.where(Repository::class.java).equalTo("id", id).findFirst()
        result?.deleteFromRealm()
        realm.commitTransaction()
    }

    fun bookmarkRepo(repo: Repository?) : Int{
        val d = this.findById(repo?.id?:0)

        if (d != null){
            this.deleteRepoById(d.id)
            return 0
        }

        this.addRepo(repo)
        return 1
    }
}