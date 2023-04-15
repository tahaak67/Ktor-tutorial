package com.example.utils

import io.ktor.http.content.*
import java.io.File
import java.util.*

fun PartData.FileItem.save(path: String): String {
    // read the file bytes
    val fileBytes = streamProvider().readBytes()
    // find the file extension eg: .jpg
    val fileExtension = originalFileName?.takeLastWhile { it != '.' }
    // generate a random name for the new file and append the file extension
    val fileName = UUID.randomUUID().toString() + "." + fileExtension
    // create our new file in the server
    val folder = File(path)
    // create parent directory if not exits
    if (!folder.parentFile.exists()) {
        folder.parentFile.mkdirs()
    }
    // continue with creating our new file
    folder.mkdir()
    // write bytes to our newly created file
    File("$path$fileName").writeBytes(fileBytes)
    return fileName
}