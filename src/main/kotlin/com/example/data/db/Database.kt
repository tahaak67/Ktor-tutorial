package com.example.data.db

import com.example.data.model.Fruit
import com.mongodb.client.model.Filters.regex
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.`in`
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.util.PatternUtil
import java.util.regex.Pattern
import kotlin.reflect.KProperty1

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

suspend fun getFruits(
    sortField: KProperty1<Fruit, String>,
    sortDirection: Int = 1,
    seasons: List<Fruit.Season>? = null,
    countries: List<String>?,
    query: String?
): List<Fruit> {
    // if season is null set it as EMPTY_BSON witch means we won't filter the results
    val seasonFilters = if (seasons.isNullOrEmpty()) EMPTY_BSON else Fruit::season `in` seasons
    val countryFilter = if (countries.isNullOrEmpty()) EMPTY_BSON else Fruit::countries `in` countries

    // use patternUtil from Kmongo to convert Options from java.regex Pattern to string options usable in mongodb
    val searchOperator = PatternUtil.getOptionsAsString(Pattern.compile("",Pattern.CASE_INSENSITIVE))
    // this version of the regex function requires that we pass the field name as a string that's why we use .name on Fruit::name
    val searchQuery = if (query.isNullOrEmpty()) EMPTY_BSON else regex(Fruit::name.name,query,searchOperator)
    return if (sortDirection < 0) {
        fruits.find(and(seasonFilters, countryFilter, searchQuery)).descendingSort(sortField).toList()
    } else {
        fruits.find(and(seasonFilters, countryFilter, searchQuery)).ascendingSort(sortField).toList()
    }
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