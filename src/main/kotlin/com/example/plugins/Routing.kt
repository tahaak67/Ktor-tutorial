package com.example.plugins

import com.example.routes.fruitRoute
import com.example.routes.simpleRoute
import com.example.utils.Constants
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        get("/h") {
            call.respondText("hello")
        }
        simpleRoute()
        fruitRoute()

        static("/") {
            // sets the base route for static routes in this block, in other words all static blocks here will start at "static/fruit_pictures/" by default instead of project root
            staticRootFolder = File(Constants.STATIC_ROOT)

            // the path the client will use to access files: /images
            static(Constants.EXTERNAL_FRUIT_IMAGE_PATH) {

                // serve all files in fruit_pictures as static content under /images
                files(Constants.FRUIT_IMAGE_DIRECTORY)
            }
        }
    }


}
