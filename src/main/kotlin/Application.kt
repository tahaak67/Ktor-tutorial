package ly.com.tahaben

import io.ktor.i18n.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
    configureRequestValidation()
    configureStatusPages()

    install(I18n){
        this.availableLanguages = listOf("en", "ar")
        this.defaultLanguage = "en"
    }
}

