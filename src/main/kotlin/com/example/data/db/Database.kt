package com.example.data.db

import com.example.data.model.Fruit
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val db = KMongo.createClient().coroutine.getDatabase("tahaben_db")
val fruits = db.getCollection<Fruit>()

suspend fun addFruit(newFruit: Fruit): Boolean {
    return try {
        // return true if insertion was successful
        fruits.insertOne(newFruit).wasAcknowledged()
    } catch (ex: Exception) {
        ex.printStackTrace()
        // return false if there is an error EX: item with same id already exists
        false
    }
}

suspend fun getFruits(): List<Fruit> {
    return fruits.find().toList()
}

suspend fun deleteFruit(fruitId: String): Boolean {
    return try {
        fruits.deleteOneById(fruitId).deletedCount == 1L
    } catch (ex: Exception) {
        ex.printStackTrace()
        false
    }
}

suspend fun updateFruit(updatedFruit: Fruit): Boolean {
    return try {
        fruits.updateOneById(id = updatedFruit.id, update = updatedFruit).wasAcknowledged()
    } catch (ex: Exception) {
        ex.printStackTrace()
        false
    }
}