package ly.com.tahaben.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val username: String,
    val token: String,
    @SerialName("expires_at")
    val expiresAt: String
)
