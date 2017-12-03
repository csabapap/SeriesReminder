package hu.csabapap.seriesreminder.data.network

import android.util.Log
import hu.csabapap.seriesreminder.BuildConfig
import hu.csabapap.seriesreminder.data.network.entities.Images
import hu.csabapap.seriesreminder.data.network.entities.LoginRequest
import hu.csabapap.seriesreminder.data.network.entities.LoginResponse
import hu.csabapap.seriesreminder.data.network.services.TvdbAuthService
import hu.csabapap.seriesreminder.data.network.services.TvdbImagesService
import io.reactivex.Flowable
import io.reactivex.Observable
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class TvdbApi {

    val loggingInterceptor : HttpLoggingInterceptor = HttpLoggingInterceptor(
            HttpLoggingInterceptor.Logger { Log.d("OkHttpTvdb", it) })
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    var okHttp: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .authenticator({ _, response ->
                if ("Bearer $token" == response.header("Authentictor")){
                    null
                }else {
                    val loginResponse: Response<LoginResponse>? = login().execute()
                    val tokenResponse: LoginResponse? = loginResponse?.body()
                    var authToken = ""
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

    var retrofit: Retrofit = Retrofit.Builder()
            .client(okHttp)
            .baseUrl("https://api.thetvdb.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    var token: String? = null
        get
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

    fun images(tvdbId : Int, type : String = "poster"): Flowable<Images>{
        return retrofit.create(TvdbImagesService::class.java).images(tvdbId, type)
    }
}