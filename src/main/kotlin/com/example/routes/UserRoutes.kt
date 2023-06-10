package com.example.routes

import com.example.data.db.addUser
import com.example.data.db.checkIfUserExists
import com.example.data.db.checkUsernameForPassword
import com.example.data.model.AccountRequest
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.security.getHashWithSalt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoute(){
    post("/register"){
        val user = try {
            call.receive<AccountRequest>()
        } catch (ex: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(success = false, message = "Invalid body"))
            return@post
        }
        // check if username already exists first
        if (checkIfUserExists(user.username)){
            // username already exists
            call.respond(HttpStatusCode.OK, SimpleResponse(success = false, message = "Username already exists"))
        }else{
            // username does not exist hash the password
            val hashedPassword = getHashWithSalt(user.password)
            // add user to db
            if (addUser(User(user.username, hashedPassword))){
                // successfully added
                call.respond(HttpStatusCode.OK, SimpleResponse(success = true, message = "Successfully added user"))
            } else {
                // some error happened
                call.respond(HttpStatusCode.OK, SimpleResponse(success = false, message = "Unknown Error"))
            }
        }
    }
    post("/login"){
        val user = try {
            call.receive<AccountRequest>()
        } catch (ex: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(success = false, message = "Invalid body"))
            return@post
        }
        val isPasswordCorrect = checkUsernameForPassword(user.username,user.password)
        if (isPasswordCorrect) {
            call.respond(HttpStatusCode.OK, SimpleResponse(success = true, message = "Login successful"))
        } else {
            call.respond(HttpStatusCode.OK, SimpleResponse(success = false, message = "Incorrect credentials"))
        }
    }
}