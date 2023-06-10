package com.example.routes

import com.example.data.db.addFruit
import com.example.data.db.deleteFruit
import com.example.data.db.getFruits
import com.example.data.db.updateFruit
import com.example.data.model.Fruit
import com.example.data.model.SimpleResponse
import com.example.utils.Constants
import com.example.utils.save
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fruitRoute() {

    get("/fruits/{sort_by?}/{sort_direction?}/{season[]?}/{country[]?}/{query?}") {
        // read the sort_by from the parameters and assign it to sortBy variable
        val sortBy = when (call.parameters["sort_by"] ?: "name") {
            "name" -> Fruit::name
            "id" -> Fruit::id
            // if the parameter sent by client does not match any values from our model return a bad request
            else -> return@get call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(success = false, message = "invalid parameter for sort_by")
            )
        }

        val sortDirection = when (call.parameters["sort_direction"] ?: "asc") {
            "dec" -> -1
            "asc" -> 1
            else -> return@get call.respond(
                HttpStatusCode.BadRequest,
                SimpleResponse(success = false, message = "invalid parameter for sort_direction")
            )
        }
        val seasons = mutableListOf<Fruit.Season>()
        call.parameters.getAll("season")?.forEach { name ->
            println("item name: $name")
            // add an item to the list of seasons
            seasons.add(
                when (name) {
                    "summer" -> Fruit.Season.Summer
                    "winter" -> Fruit.Season.Winter
                    "autumn" -> Fruit.Season.Autumn
                    "spring" -> Fruit.Season.Spring
                    else -> return@get call.respond(
                        HttpStatusCode.BadRequest,
                        SimpleResponse(success = false, message = "invalid parameter $name for season")
                    )
                }
            )
        }

        // list of countries
        val countries = call.parameters.getAll("country")

        // search query
        val query = call.parameters["query"]

        // get fruits from the database
        call.respond(
            HttpStatusCode.OK,
            getFruits(
                sortField = sortBy,
                sortDirection = sortDirection,
                seasons = seasons,
                countries = countries,
                query = query
            )
        )
    }
    authenticate {
        post("/add-fruit") {
            try {

                // get the username for the authenticated user
                val username = call.principal<UserIdPrincipal>()?.name ?: throw Exception("Can't get username")

                // receive multipart data from the client
                val multipart = call.receiveMultipart()

                // define variable to hold our parameters data
                var fileName: String? = null
                var name: String? = null
                var season: String? = null
                val countries: MutableList<String> = mutableListOf()
                var imageUrl: String? = null

                try {
                    // loop through each part of our multipart
                    multipart.forEachPart { partData ->
                        when (partData) {
                            is PartData.FormItem -> {
                                // to read parameters that we sent with the image
                                when (partData.name) {
                                    "name" -> name = partData.value
                                    "season" -> season = partData.value
                                    "countries" -> countries.add(partData.value)
                                }
                            }

                            is PartData.FileItem -> {
                                // to read the image data we call the 'save' utility function passing our path
                                if (partData.name == "image") {
                                    fileName = partData.save(Constants.FRUIT_IMAGE_PATH)
                                    imageUrl = "${Constants.BASE_URL}${Constants.EXTERNAL_FRUIT_IMAGE_PATH}/$fileName"
                                }
                            }

                            else -> Unit
                        }
                    }
                } catch (ex: Exception) {
                    // something went wrong with the image part, delete the file
                    File("${Constants.FRUIT_IMAGE_PATH}/$fileName").delete()
                    ex.printStackTrace()
                    call.respond(HttpStatusCode.InternalServerError, "Error")
                }
                // create a new fruit object using data we collected above
                val newFruit = Fruit(
                    name = name!!,
                    // the valueOf function will find the enum class type that matches this string and return it, an Exception is thrown if the string does not match any type
                    season = Fruit.Season.valueOf(season!!),
                    countries = countries,
                    image = imageUrl,
                    addedBy = username
                )
                // add the received fruit to the database
                if (!addFruit(newFruit)) {
                    // if not added successfully return with an error
                    return@post call.respond(
                        HttpStatusCode.Conflict,
                        SimpleResponse(success = false, message = "Item already exits")
                    )
                }

                // acknowledge that we successfully added the fruit by responding
                call.respond(HttpStatusCode.Created, newFruit)
            } catch (ex: Exception) {

                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid data"))
            }

        }
    }

    patch("/add-fruit") {
        try {
            val newFruit = call.receive<Fruit>()
            if (!updateFruit(newFruit)) {
                return@patch call.respond(
                    HttpStatusCode.NotFound,
                    SimpleResponse(success = false, message = "Please check the fruit id")
                )
            }
            call.respond(
                HttpStatusCode.OK, SimpleResponse(
                    true,
                    "Successfully updated ${newFruit.name}"
                )
            )
        } catch (ex: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid Fruit format"))
        }
    }

    delete("/delete-fruit/&{id?}") {
        try {
            val id = call.parameters["id"]
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(success = false, message = "Parameter id is required")
                )
            if (!deleteFruit(id)) {
                return@delete call.respond(
                    HttpStatusCode.NotFound,
                    SimpleResponse(success = false, message = "Please check the fruit id")
                )
            }
            call.respond(
                HttpStatusCode.OK, SimpleResponse(
                    true,
                    "Successfully deleted $id"
                )
            )
        } catch (ex: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid Fruit format"))
        }
    }
}