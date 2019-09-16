package cruz.dariel.com.githubrepos.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import cruz.dariel.com.githubrepos.R

import cruz.dariel.com.githubrepos.adapters.DisplayAdapter
import cruz.dariel.com.githubrepos.app.Constants
import cruz.dariel.com.githubrepos.app.MyApplication
import cruz.dariel.com.githubrepos.extensions.showErrorMessage
import cruz.dariel.com.githubrepos.extensions.toast
import cruz.dariel.com.githubrepos.models.Repository
import cruz.dariel.com.githubrepos.models.SearchResponse
import cruz.dariel.com.githubrepos.models.repos.RepositoryRepo
import cruz.dariel.com.githubrepos.retrofit.GithubAPIService

import java.util.HashMap

import kotlinx.android.synthetic.main.activity_display.*
import kotlinx.android.synthetic.main.header.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class DisplayActivity : AppCompatActivity(), DisplayAdapter.Listener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var displayAdapter: DisplayAdapter
    private var browsedRepositories: List<Repository> = mutableListOf()

    @Inject
    lateinit var githubApiService: GithubAPIService

    @Inject
    lateinit var repositoryRepo: RepositoryRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        MyApplication().getComponent(applicationContext).inject(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Showing Browsed Results"

        setAppUserName()

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
        displayAdapter = DisplayAdapter(this, this, items)
        recyclerView.adapter = displayAdapter
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {

        menuItem.isChecked = true

        when (menuItem.itemId) {

            R.id.item_bookmark -> { consumeMenuEvents(repositoryRepo.findAll(), "Showing Bookmarks")}

            R.id.item_browsed_results -> {consumeMenuEvents(browsedRepositories,"Showing Browsed Results")}
        }

        return true
    }

    private inline fun consumeMenuEvents(items: List<Repository>, title: String){
        closeDrawer()
        displayAdapter.swap(items)
        supportActionBar!!.title = title
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

    override fun onItemClicked(item: Repository?){
        item?.let {
            val url = it.htmlUrl
            val webpage = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
    }

    override fun onBookmarkImgClicked(item: Repository?) {
        val action = repositoryRepo.bookmarkRepo(item)

        if(action == 1){
            toast("Added to Bookmarks")
        }else{
            toast("Removed from Bookmarks")
        }
    }


    companion object {

        private val TAG = DisplayActivity::class.java.simpleName
    }
}
