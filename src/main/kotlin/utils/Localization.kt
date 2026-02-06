package ly.com.tahaben.utils

import io.ktor.i18n.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.text.MessageFormat

/**
 * Created by Taha Ben Ashur (https://github.com/tahaak67) on 05, Feb, 2026
 */

fun RoutingContext.translate(key: String, vararg args: String?): String {
    val message = i18n(key)
    return if(args.isEmpty()){
        message
    } else {
        MessageFormat.format(message, *args)
    }
}

fun ApplicationCall.translate(key: String, vararg args: String?): String {
    val message = i18n(key)
    return if(args.isEmpty()){
        message
    } else {
        MessageFormat.format(message, *args)
    }
}
