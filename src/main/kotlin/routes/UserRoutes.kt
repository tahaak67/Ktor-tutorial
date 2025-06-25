package ly.com.tahaben.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import ly.com.tahaben.data.database.addUser
import ly.com.tahaben.data.database.checkIfUsernameExists
import ly.com.tahaben.data.database.checkUsernameForPassword
import ly.com.tahaben.data.model.AccountRequest
import ly.com.tahaben.data.model.LoginResponse
import ly.com.tahaben.data.model.SimpleResponse
import ly.com.tahaben.data.model.User
import ly.com.tahaben.utils.getHashWithSalt
import kotlin.time.Duration.Companion.days


fun Route.userRoutes() {

    post("/register"){
        try {
            val accountRequest = call.receive<AccountRequest>()
            val user = User(accountRequest.username, getHashWithSalt(accountRequest.password))
            if (checkIfUsernameExists(user.username)){
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Username already exists"))
            }else if (addUser(user)) {
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Account Created successfully"))
            }else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Error please try again"))
            }
        } catch (e: ContentTransformationException){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Username and password are required"))
        }
        catch (e: Exception){
            e.printStackTrace()
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something went wrong"))
        }
    }
    val jwtConfig = environment.config.config("jwt")
    val jwtAudience = jwtConfig.property("audience").getString()
    val jwtDomain = jwtConfig.property("domain").getString()
    val jwtRealm = jwtConfig.property("realm").getString()
    val jwtSecret = jwtConfig.property("secret").getString()
    post("/login"){
        try {
            val accountRequest = call.receive<AccountRequest>()


            if (checkUsernameForPassword(accountRequest.username, accountRequest.password)){
                val expiresAt = Clock.System.now().plus(30.days)
                val token = JWT.create()
                    .withIssuer(jwtDomain)
                    .withAudience(jwtAudience)
                    .withClaim("username", accountRequest.username)
                    .withExpiresAt(expiresAt.toJavaInstant())
                    .sign(Algorithm.HMAC256(jwtSecret))
                call.respond(HttpStatusCode.OK,
                    LoginResponse(
                        token = token,
                        username = accountRequest.username,
                        expiresAt = expiresAt.toLocalDateTime(
                            TimeZone.UTC
                        ).toString()
                    ))
            }else {
                call.respond(HttpStatusCode.OK, SimpleResponse(false, "Username or password is incorrect"))
            }

        } catch (e: ContentTransformationException){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Username and password are required"))
        }
        catch (e: Exception){
            e.printStackTrace()
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Something went wrong"))
        }
    }
}



