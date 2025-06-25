package ly.com.tahaben.data.database

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.netty.channel.unix.Limits
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import ly.com.tahaben.data.model.Fruit
import ly.com.tahaben.data.model.FruitPage
import ly.com.tahaben.data.model.Season
import ly.com.tahaben.data.model.User
import ly.com.tahaben.utils.checkHashForPassword
import kotlin.math.ceil


val client = MongoClient.create("mongodb://localhost:27017")
val db = client.getDatabase("tahaben_db")
val fruitCollection = db.getCollection<Fruit>("fruit")
val usersCollection = db.getCollection<User>("users")

suspend fun addFruit(fruit: Fruit): Boolean{
    return try {
        fruitCollection.insertOne(fruit).wasAcknowledged()
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}

suspend fun getFruits(
     sortingField: String = Fruit::name.name,
     sortingDirection: Int = 1,
     season: List<Season>? = null,
     query: String? = null,
     limit: Int = 10,
     page: Int = 1
): FruitPage {
    val sorting = if (sortingDirection > 0) Sorts.ascending(sortingField) else Sorts.descending(sortingField)
    val seasonFilter = if (season == null || season.isEmpty()) Filters.empty() else Filters.`in`(Fruit::season.name, season)
    val queryFilter = if (query == null) Filters.empty() else Filters.regex(Fruit::name.name, query, "i")
    val filter = Filters.and(seasonFilter, queryFilter)
    val total = fruitCollection.countDocuments(filter)
    val skip = (page - 1) * limit

    val fruits = fruitCollection.find(filter).skip(skip).limit(limit).sort(sorting).toList()

    return FruitPage(
        fruits = fruits,
        page = page,
        total = total,
        pageSize = limit,
        totalPages = ceil(total.toDouble() / limit).toLong()
    )
}

suspend fun updateFruit(fruit: Fruit): Boolean{
    val filter = Filters.eq("_id",fruit.id)
    return fruitCollection.replaceOne(filter, fruit).modifiedCount == 1L
}

suspend fun deleteFruitById(fruitId: String): Boolean{
    val filter = Filters.eq("_id", fruitId)
    return fruitCollection.deleteOne(filter).deletedCount == 1L
}

suspend fun addUser(user: User): Boolean{
    return try {
        usersCollection.insertOne(user).wasAcknowledged()
    }catch (e: Exception){
        e.printStackTrace()
        false
    }
}

suspend fun checkIfUsernameExists(username: String): Boolean{
    val filter = Filters.eq("_id", username)
    return usersCollection.find(filter).limit(1).firstOrNull() != null
}

suspend fun checkUsernameForPassword(username: String, password: String): Boolean {
    val filter = Filters.eq("_id", username)
    val actualPassword = usersCollection.find(filter).limit(1).firstOrNull()?.password ?: return false
    return checkHashForPassword(password, actualPassword)
}
