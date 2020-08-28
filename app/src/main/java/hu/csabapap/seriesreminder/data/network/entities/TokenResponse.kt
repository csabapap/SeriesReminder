package hu.csabapap.seriesreminder.data.network.entities

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TokenResponse(@field:Json(name = "access_token")
                         val accessToken: String = "",
                         @field:Json(name = "refresh_token")
                         val refreshToken: String = "",
                         @field:Json(name = "scope")
                         val scope: String = "",
                         @field:Json(name = "created_at")
                         val createdAt: Int = 0,
                         @field:Json(name = "token_type")
                         val tokenType: String = "",
                         @field:Json(name = "expires_in")
                         val expiresIn: Int = 0)