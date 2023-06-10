package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Fruit(
    val name: String,
    val season: Season = Season.Unknown,
    val countries: List<String> = emptyList(),
    @BsonId
    val id: String = ObjectId().toString(),
    val image: String?,
    @SerialName("added_by")
    val addedBy: String = "Unknown"
){
    enum class Season {
        Spring, Winter, Summer, Autumn, Unknown
    }
}
