package ly.com.tahaben.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SimpleResponse(
    val isSuccess: Boolean,
    val message: String,
)
