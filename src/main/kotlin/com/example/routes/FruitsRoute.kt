package com.example.routes

import com.example.data.model.Fruit
import com.example.data.model.SimpleResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.fruitRoute() {
    // create a mutable list of fruits
    val fruits = mutableListOf(
        Fruit("apple", "1"),
        Fruit("orange", "2"),
        Fruit("banana", "3"),
        Fruit("pineapple", "4"),
        Fruit("tomato", "5"),
    )

    get("/fruits/{id?}") {
        // get the id from the parameters if no id is passed return the full list
        val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.OK, fruits)

        // define the status code we would like to return, and return the list which will be converted automatically to Json
        call.respond(HttpStatusCode.OK, fruits.filter { it.id == id })
    }
    post("/add-fruit") {
        try {
            // receive the fruit from the user
            val newFruit = call.receive<Fruit>()

            // add the received fruit to the list
            fruits.add(newFruit)

            // acknowledge that we successfully added the fruit by responding
            call.respond(
                HttpStatusCode.OK, SimpleResponse(
                    true,
                    "Successfully added ${newFruit.name}"
                )
            )
        }catch (ex: Exception){

            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Invalid Fruit format"))
        }

    }
}