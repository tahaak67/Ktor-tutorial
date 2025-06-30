package ly.com.tahaben

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.resolveResource
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import ly.com.tahaben.data.model.SimpleResponse
import kotlin.text.contains


fun Application.configureStatusPages(){
    install(StatusPages) {
        handleNotFound()
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
}


fun StatusPagesConfig.handleNotFound(){
    status(HttpStatusCode.NotFound){ call, code ->
        // Read request the header
        val acceptHeader = call.request.headers[HttpHeaders.Accept] ?: ""

        // if the accept header contains "text/html" respond with the 404 page
        if (acceptHeader.contains(ContentType.Text.Html.toString())){

            // read the html page from resources
            val page = call.resolveResource("static/error404.html")
            if (page != null){
                call.respond(page)
            }else {
                call.respond(HttpStatusCode.InternalServerError)
            }

        } else {
            // respond with a json
            call.respond(code, SimpleResponse(false, "Sorry the page you're looking for is not found :("))
        }
    }
}