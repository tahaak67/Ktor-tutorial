package com.example.plugins

import com.example.routes.fruitRoutes
import com.example.routes.simpleRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/h"){
            call.respondText("hello")
        }
        simpleRoute()
        fruitRoutes()
    }
}
