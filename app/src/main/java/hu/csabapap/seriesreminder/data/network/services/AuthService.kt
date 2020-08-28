package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.TokenResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun requestToken(@Field("code") code: String,
                     @Field("client_id") clientId: String,
                     @Field("client_secret") clientSecret: String,
                     @Field("redirect_uri") redirectUri: String,
                     @Field("grant_type") grantType: String = "authorization_code"
    ): TokenResponse

}