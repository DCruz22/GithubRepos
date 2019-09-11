package cruz.dariel.com.githubrepos.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import cruz.dariel.com.githubrepos.app.Constants
import cruz.dariel.com.githubrepos.retrofit.RetrofitClient
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [ApiModule::class])
class GithubServiceModule{

    @Provides
    fun provideRetrofitClient(retrofit: Retrofit) : RetrofitClient{
        return retrofit.create(RetrofitClient::class.java)
    }
}

@Module
class ApiModule{

    @Provides
    fun provideGson() : Gson {
        val gson = GsonBuilder()
        return gson.create()
    }

    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}