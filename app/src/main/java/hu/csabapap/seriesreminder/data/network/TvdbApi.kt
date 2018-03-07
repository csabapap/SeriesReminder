package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.network.entities.Images
import hu.csabapap.seriesreminder.data.network.entities.LoginRequest
import hu.csabapap.seriesreminder.data.network.entities.LoginResponse
import hu.csabapap.seriesreminder.data.network.entities.TvdbEpisode
import hu.csabapap.seriesreminder.data.network.services.TvdbAuthService
import hu.csabapap.seriesreminder.data.network.services.TvdbEpisodeService
import hu.csabapap.seriesreminder.data.network.services.TvdbImagesService
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class TvdbApi {

    private val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { Timber.tag("OkHttpTvdb").d(it) })
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private var okHttp: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .authenticator({ _, response ->
                if ("Bearer $token" == response.header("Authentictor")){
                    null
                }else {
                    val loginResponse: Response<LoginResponse>? = login().execute()
                    val tokenResponse: LoginResponse? = loginResponse?.body()
                    val authToken: String
                    if (loginResponse != null && loginResponse.isSuccessful
                            && tokenResponse != null) {
                        authToken = tokenResponse.token
                        token = authToken
                    }
                    response.request().newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                }
            })
            .build()

    private var retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://api.thetvdb.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    var token: String? = null
        set(value){
            field = value
            reInitRetrofit()
        }

    private fun reInitRetrofit(){
        okHttp = okHttp.newBuilder()
                .addInterceptor({ chain ->
                    val request = chain.request()
                    val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                    chain.proceed(newRequest)
                })
                .build()

        retrofit = retrofit.newBuilder()
                .client(okHttp)
                .build()
    }

    fun login() : Call<LoginResponse> {
        val loginRequest = LoginRequest(BuildConfig.TVDB_CLIENT_ID)
        return retrofit.create(TvdbAuthService::class.java).login(loginRequest)
    }

    fun images(tvdbId : Int, type : String = "poster"): Single<Images>{
        return retrofit.create(TvdbImagesService::class.java).images(tvdbId, type)
    }

    fun episode(tvdbId: Int): Single<TvdbEpisode>{
        return retrofit.create(TvdbEpisodeService::class.java).episode(tvdbId)
    }
}