package ly.com.tahaben.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId

@Serializable
data class Fruit(
    @SerialName("_id")
    val id: String = ObjectId().toHexString(),
    val name: String,
    val countries: List<String>,
    val season: Season,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("added_by")
    val addedBy: String?
)

enum class Season {
    SUMMER, WINTER, AUTUMN, SPRING, UNKNOWN
}