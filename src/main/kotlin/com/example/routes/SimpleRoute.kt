package com.example.routes

import com.example.data.model.SimpleResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.simpleRoute(){

    route("/simple"){

        get {
            val simpleObject = SimpleResponse(true, "nothing")
            call.respond(HttpStatusCode.OK, simpleObject)
        }

    }

}