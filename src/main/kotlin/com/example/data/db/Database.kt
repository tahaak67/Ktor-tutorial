package com.example.data.db

import com.example.data.model.Fruit
import com.example.data.model.User
import com.example.security.checkHashForPassword
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.regex
import com.mongodb.client.model.Sorts
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import java.util.regex.Pattern
import kotlin.reflect.KProperty1

val db = MongoClient.create("mongodb://localhost:27017").getDatabase("tahaben_db")
val fruits = db.getCollection<Fruit>("Fruit")
val users = db.getCollection<User>("User")

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

suspend fun getFruits(
    sortField: KProperty1<Fruit, String>,
    sortDirection: Int = 1,
    seasons: List<Fruit.Season>? = null,
    countries: List<String>?,
    query: String?
): List<Fruit> {
    // if season is null set it as Filters.empty witch means we won't filter the results
    val seasonFilters = if (seasons.isNullOrEmpty()) Filters.empty() else Filters.`in`(Fruit::season.name, seasons)
    val countryFilter = if (countries.isNullOrEmpty()) Filters.empty() else Filters.`in`(Fruit::countries.name, countries)

    // this version of the regex function requires that we pass the field name as a string that's why we use .name on Fruit::name
    val searchQuery = if (query.isNullOrEmpty()) Filters.empty() else Filters.regex(Fruit::name.name,query,"i")
    return if (sortDirection < 0) {
        fruits.find(Filters.and(seasonFilters, countryFilter, searchQuery)).sort(Sorts.descending(sortField.name)).toList()
    } else {
        fruits.find(Filters.and(seasonFilters, countryFilter, searchQuery)).sort(Sorts.ascending(sortField.name)).toList()
    }
}

suspend fun deleteFruit(fruitId: String): Boolean {
    return try {
        val deleteFilter = Filters.eq("_id", fruitId)
        fruits.deleteOne(deleteFilter).deletedCount == 1L
    } catch (ex: Exception) {
        ex.printStackTrace()
        false
    }
}

suspend fun updateFruit(updatedFruit: Fruit): Boolean {
    return try {
        val updateFilter = Filters.eq("_id", updatedFruit.id)
        fruits.replaceOne(filter = updateFilter, replacement =  updatedFruit).wasAcknowledged()
    } catch (ex: Exception) {
        ex.printStackTrace()
        false
    }
}

suspend fun addUser(user: User): Boolean {
    return try {
        users.insertOne(user).wasAcknowledged()
    } catch (ex: Exception) {
        ex.printStackTrace()
        false
    }
}

suspend fun checkIfUserExists(username: String): Boolean {
    val userNameFilter = Filters.eq("_id", username)
    return users.find(userNameFilter).limit(1).firstOrNull() != null
}

suspend fun checkUsernameForPassword(username: String, passwordToCheck: String): Boolean {
    val userFilter = Filters.eq("_id", username)
    // return false if we can't find the username in our db
    val actualPassword = users.find(userFilter).firstOrNull()?.password ?: return false
    return checkHashForPassword(passwordToCheck, actualPassword)
}