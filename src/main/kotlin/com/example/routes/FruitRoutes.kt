package com.example.routes

import com.example.data.db.*
import com.example.data.model.Fruit
import com.example.data.model.SimpleResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fruitRoute() {

    get("/fruits/{id?}") {
        // get fruits from the database
        call.respond(HttpStatusCode.OK, getFruits())
    }
    post("/add-fruit") {
        try {
            // receive the fruit from the user
            val newFruit = call.receive<Fruit>()

            // add the received fruit to the database
            if (!addFruit(newFruit)) {
                // if not added successfully return with an error
                return@post call.respond(
                    HttpStatusCode.Conflict,
                    SimpleResponse(success = false, message = "Item already exits")
                )
            }

            // acknowledge that we successfully added the fruit by responding
            call.respond(
                HttpStatusCode.Created, SimpleResponse(
                    true,
                    "Successfully added ${newFruit.name}"
                )
            )
        } catch (ex: Exception) {

            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Invalid Fruit format"))
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