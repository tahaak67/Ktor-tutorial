package com.example.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class User(
    @SerialName("_id")
    val username: String,
    val password: String
)
