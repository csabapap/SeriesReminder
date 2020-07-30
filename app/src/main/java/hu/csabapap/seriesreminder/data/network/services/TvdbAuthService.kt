package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.LoginRequest
import hu.csabapap.seriesreminder.data.network.entities.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TvdbAuthService {

    @POST("/login")
    fun login(@Body loginRequest: LoginRequest) : Call<LoginResponse>

}