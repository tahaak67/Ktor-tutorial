package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Fruit(
    val name: String,
    @BsonId
    val id: String = ObjectId().toString()
)
