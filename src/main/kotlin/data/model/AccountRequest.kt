package ly.com.tahaben.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountRequest(
    @SerialName("_id")
    val username: String,
    val password: String,
)
