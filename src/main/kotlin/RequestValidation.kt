package ly.com.tahaben

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import ly.com.tahaben.data.model.Fruit

fun Application.configureRequestValidation(){
    install(RequestValidation){
        validateFruitBody()
    }
}

fun RequestValidationConfig.validateFruitBody(){
    validate<Fruit>{ fruit ->
        val errors = mutableListOf<String>()
        // Validate name
        if (fruit.name.isBlank()) {
            errors.add("Name cannot be empty or blank")
        } else if (fruit.name.length > 100) {
            errors.add("Name cannot exceed 100 characters")
        }

        // Validate countries
        if (fruit.countries.isEmpty()) {
            errors.add("At least one country must be specified")
        } else {
            if (fruit.countries.any { it.isBlank() }) {
                errors.add("Country names cannot be empty or blank")
            }

            if (fruit.countries.any { it.length > 50 }) {
                errors.add("Country names cannot exceed 50 characters")
            }
        }

        // Validate image not blank if provided
        fruit.imageUrl?.let { url ->
            if (url.isBlank()) {
                errors.add("Image URL cannot be blank if provided")
            }
        }


        if (errors.isEmpty()){
            ValidationResult.Valid
        }else ValidationResult.Invalid(errors)
    }
}

suspend fun validateAddNewFruitMultipart(multipart: MultiPartData){

    val errors = mutableListOf<String>()
    var name: String? = null
    val countries = mutableListOf<String>()

    multipart.forEachPart { part ->
        when(part){
            is PartData.FormItem -> {
                when(part.name){
                    "name" -> name = part.value
                    "country" -> countries.add(part.value)
                }
            }

            else -> Unit
        }
    }

    if (name?.isEmpty() == true){
        errors.add("Name field is required")
    }
    if (countries.isEmpty()){
        errors.add("Fruit must have at least one country")
    }

    if (errors.isNotEmpty()){
        throw RequestValidationException(reasons = errors, value = Unit)
    }

}