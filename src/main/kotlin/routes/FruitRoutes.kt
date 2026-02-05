package ly.com.tahaben.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ly.com.tahaben.data.database.addFruit
import ly.com.tahaben.data.database.deleteFruitById
import ly.com.tahaben.data.database.getFruits
import ly.com.tahaben.data.database.updateFruit
import ly.com.tahaben.data.model.Fruit
import ly.com.tahaben.data.model.Season
import ly.com.tahaben.data.model.SimpleResponse
import ly.com.tahaben.validateAddNewFruitMultipart


fun Route.fruitRoutes() {


    get("/get-fruit") {
        val sortingField = when (call.queryParameters["sorting_field"]) {
            "name" -> "name"
            "country" -> "country"
            "season" -> "season"
            "id" -> "_id"
            null -> "name"
            else -> return@get call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid sorting field"))
        }
        val sortingDirection = when (call.queryParameters["sorting_direction"]) {
            "asc" -> 1
            "desc" -> -1
            null -> 1
            else -> return@get call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(false, "Invalid value for sorting_direction")
            )
        }
        val seasons = mutableListOf<Season>()
        call.queryParameters.getAll("season")?.forEach { season ->
            when (season) {
                "summer" -> seasons.add(Season.SUMMER)
                "winter" -> seasons.add(Season.WINTER)
                "spring" -> seasons.add(Season.SPRING)
                "autumn" -> seasons.add(Season.AUTUMN)
                "unknown" -> seasons.add(Season.UNKNOWN)
                else -> return@get call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid season value"))
            }
        }

        val q = call.queryParameters["q"]
        val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
        val page = call.queryParameters["page"]?.toIntOrNull() ?: 1

        val fruits = getFruits(
            sortingField = sortingField,
            sortingDirection = sortingDirection,
            season = seasons,
            query = q,
            limit = limit,
            page = page
        )


        call.respond(status = HttpStatusCode.OK, message = fruits)
    }

    authenticate("jwt") {
        post("/add-fruit") {

                val multipart = call.receiveMultipart()
                val fruit = validateAddNewFruitMultipart(multipart)

                val addedBy = call.principal<JWTPrincipal>()?.get("username") ?: "Unknown"


            val newFruit = fruit.copy(addedBy = addedBy)
                if (addFruit(newFruit)) {

                    call.respond(HttpStatusCode.Created, newFruit)
                } else {
                    call.respond(HttpStatusCode.BadRequest, SimpleResponse(isSuccess = false, message = "Cant add fruit"))
                }

        }
    }

    patch("/add-fruit") {

            val newFruit = call.receive<Fruit>()

            if (updateFruit(newFruit)) {

                call.respond(HttpStatusCode.Created, newFruit)
            } else {
                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(isSuccess = false, message = "Cant update fruit")
                )
            }

    }

    delete("/delete-fruit/{id}") {
        val fruitId = call.pathParameters["id"] ?: return@delete call.respond(
            HttpStatusCode.BadRequest,
            SimpleResponse(false, "Must provide an id")
        )

        if (deleteFruitById(fruitId)) {
            call.respond(HttpStatusCode.OK, SimpleResponse(true, "Deleted Successfully"))
        } else {
            call.respond(HttpStatusCode.OK, SimpleResponse(false, "Error can't delete fruit"))
        }
    }

    get("/exception"){
        throw Exception("This is a test")
    }
}
