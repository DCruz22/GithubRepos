package cruz.dariel.com.githubrepos.activities

import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.sriyank.githubrepos.R

import cruz.dariel.com.githubrepos.adapters.DisplayAdapter
import cruz.dariel.com.githubrepos.app.Constants
import cruz.dariel.com.githubrepos.extensions.showErrorMessage
import cruz.dariel.com.githubrepos.extensions.toast
import cruz.dariel.com.githubrepos.models.Repository
import cruz.dariel.com.githubrepos.models.SearchResponse
import cruz.dariel.com.githubrepos.retrofit.GithubAPIService
import cruz.dariel.com.githubrepos.retrofit.RetrofitClient
import io.realm.Realm

import java.util.HashMap

import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DisplayActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var displayAdapter: DisplayAdapter
    private var browsedRepositories: List<Repository> = mutableListOf()
    private val githubApiService: GithubAPIService by lazy {
        RetrofitClient.githubAPIService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Showing Browsed Results"

        setAppUserName();

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        nav_view.setNavigationItemSelectedListener(this)

        val drawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val intent = intent
        if (intent.getIntExtra(Constants.KEY_QUERY_TYPE, -1) == Constants.SEARCH_BY_REPO) {
            val queryRepo = intent.getStringExtra(Constants.KEY_REPO_SEARCH)
            val repoLanguage = intent.getStringExtra(Constants.KEY_LANGUAGE)
            fetchRepositories(queryRepo, repoLanguage)
        } else {
            val githubUser = intent.getStringExtra(Constants.KEY_GITHUB_USER)
            fetchUserRepositories(githubUser)
        }
    }

    private fun setAppUserName() {
        val sp = getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val personName = sp.getString(Constants.KEY_PERSON_NAME, "User")

        val headerView = nav_view.getHeaderView(0)
        headerView.txvName.text = personName
    }

    private fun fetchUserRepositories(githubUser: String) {

        githubApiService.searchRepositoriesByUser(githubUser).enqueue(object: Callback<List<Repository>> {

            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {

                if(response.isSuccessful){
                    Log.i(TAG, "posts loaded from API ${response}")

                    response.body()?.let {
                        browsedRepositories = it
                    }

                    if(browsedRepositories.isNotEmpty()){
                        setupRecyclerView(browsedRepositories);
                    }else{
                        toast("No items found")
                    }

                }else{
                    showErrorMessage(response.errorBody()!!)
                }

            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                toast(t.message ?: "Error fetching results")
            }
        })
    }

    private fun fetchRepositories(queryRepo: String, repoLanguage: String) {
        var queryRepo = queryRepo

        val query = HashMap<String, String>()

        if (repoLanguage.isNotEmpty())
            queryRepo += " language:$repoLanguage"
        query["q"] = queryRepo

        githubApiService.searchRepositories(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "posts loaded from API $response")

                    response.body()?.items?.let {
                        browsedRepositories = it
                    }

                    if (browsedRepositories.isNotEmpty())
                        setupRecyclerView(browsedRepositories)
                    else
                        toast("No Items Found")

                } else {
                    Log.i(TAG, "error $response")
                    showErrorMessage(response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                toast(t.toString(), Toast.LENGTH_LONG)
            }
        })
    }

    private fun setupRecyclerView(items: List<Repository>) {
        displayAdapter = DisplayAdapter(this, items)
        recyclerView.adapter = displayAdapter
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        menuItem.isChecked = true

        when (menuItem.itemId) {

            R.id.item_bookmark -> { consumeMenuEvents({showBookmarks()}, "Showing Bookmarks")}

            R.id.item_browsed_results -> {consumeMenuEvents({ showBrowsedResults()},"Showing Browsed Results")}
        }

        return true
    }

    private inline fun consumeMenuEvents(myFunc: () -> Unit, title: String){
        closeDrawer()
        myFunc()
        supportActionBar!!.title = title
    }

    private fun showBrowsedResults() {
        displayAdapter.swap(browsedRepositories)
    }

    private fun showBookmarks() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction { realm ->
            val bookMarkedRepoList =  realm.where(Repository::class.java).findAll()
            displayAdapter.swap(bookMarkedRepoList)
        }
    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            closeDrawer()
        else {
            super.onBackPressed()
        }
    }

    companion object {

        private val TAG = DisplayActivity::class.java.simpleName
    }
}
