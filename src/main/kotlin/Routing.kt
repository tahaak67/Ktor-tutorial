package ly.com.tahaben

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ly.com.tahaben.data.model.SimpleResponse
import ly.com.tahaben.routes.fruitRoutes
import ly.com.tahaben.routes.userRoutes
import ly.com.tahaben.utils.Constants
import java.io.File


fun Application.configureRouting() {
    routing {
        get("/hello") {
            call.respondText("Hello World!")
        }
        get("/simple"){
            call.respond(status = HttpStatusCode.OK, message = SimpleResponse(isSuccess = true, message = "Hello from simple"))
        }
        fruitRoutes()
        userRoutes()
        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")
        staticFiles(remotePath = Constants.EXTERNAL_IMAGE_PATH, dir = File(Constants.STATIC_FRUIT_IMAGE_PATH))
    }
}
