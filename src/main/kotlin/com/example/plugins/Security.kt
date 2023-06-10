package com.example.plugins

import com.example.data.db.checkUsernameForPassword
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureBasicAuthentication() {
    authentication {
        basic {
            realm = "Fruits Server"
            validate { credentials ->
                if (checkUsernameForPassword(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}