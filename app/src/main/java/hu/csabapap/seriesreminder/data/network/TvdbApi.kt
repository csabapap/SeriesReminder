package hu.csabapap.seriesreminder.data.network

import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.network.entities.*
import hu.csabapap.seriesreminder.data.network.services.TvdbAuthService
import hu.csabapap.seriesreminder.data.network.services.TvdbEpisodeService
import hu.csabapap.seriesreminder.data.network.services.TvdbImagesService
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class TvdbApi {

    private val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { Timber.tag("OkHttpTvdb").d(it) })
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private var okHttp: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .authenticator { _, response ->
                if ("Bearer $token" == response.header("Authorization")){
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
            }
            .build()

    private var retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://api.thetvdb.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    var token: String? = null
        set(value){
            field = value
            reInitRetrofit()
        }

    private fun reInitRetrofit(){
        okHttp = okHttp.newBuilder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $token")
                            .build()
                    chain.proceed(newRequest)
                }
                .build()

        retrofit = retrofit.newBuilder()
                .client(okHttp)
                .build()
    }

    private fun login() : Call<LoginResponse> {
        val loginRequest = LoginRequest(BuildConfig.TVDB_CLIENT_ID)
        return retrofit.create(TvdbAuthService::class.java).login(loginRequest)
    }

    fun imagesCall(tvdbId : Int, type : String = "poster"): Call<Images>{
        return retrofit.create(TvdbImagesService::class.java).imagesCall(tvdbId, type)
    }

    suspend fun images(tvdbId : Int, type : String = "poster"): Images? {
        return retrofit.create(TvdbImagesService::class.java).images(tvdbId, type)
    }

    fun imagesSingle(tvdbId : Int, type : String = "poster"): Single<Images>{
        return retrofit.create(TvdbImagesService::class.java).imagesSingle(tvdbId, type)
    }

    suspend fun episode(tvdbId: Int): EpisodeData{
        return retrofit.create(TvdbEpisodeService::class.java).episode(tvdbId)
    }

    fun episodeCall(tvdbId: Int): Call<EpisodeData>{
        return retrofit.create(TvdbEpisodeService::class.java).episodeCall(tvdbId)
    }
}