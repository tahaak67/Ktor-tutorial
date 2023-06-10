package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountRequest(
    val username: String,
    val password: String
)
