package com.example.empty3.api

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val email: String,
    val username: String,
    val password: String
)

