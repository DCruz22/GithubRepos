package cruz.dariel.com.githubrepos.models.repos

import cruz.dariel.com.githubrepos.models.Repository
import io.realm.Realm

class RepositoryRepo(private val realm: Realm) {

    fun find(id: int): Repository? {
        return realm.where(Repository::class.java).equalTo("id", id).findFirst()
    }

    fun findAll(): List<Repository> {
        return realm.where(Repository::class.java).findAll()
    }

    fun addRepo(item: Repository?) {
        current?.let {
            realm.executeTransactionAsync ( {
                realm -> realm.copyToRealmOrUpdate(current)
            }, {
                    context.toast("Bookmarked Successfully")
                }, {
                    context.toast("Error Ocurred")
                }
            )
        }
    }

    fun deleteById(id: int) {
        realm.beginTransaction()
        val results = realm.where(NoteDto::class.java!!).equalTo("id", id).findAll()
        results.deleteAllFromRealm()
        realm.commitTransaction()
    }

}