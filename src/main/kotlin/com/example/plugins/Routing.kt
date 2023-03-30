package com.example.plugins

import com.example.routes.fruitRoute
import com.example.routes.simpleRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/h"){
            call.respondText("hello")
        }
        simpleRoute()
        fruitRoute()
    }
}
