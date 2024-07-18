package com.example.empty3.api

import kotlinx.serialization.Serializable


@Serializable
data class Comment(
    val username: String,
    val rating: Int,
    val comment: String
)

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Long = 0L,
    var rating: Long = 0L,
    var comments: List<Comment> = emptyList(),
    var commentCount: Int = 0,
    var reviewCount: Int = 0,
    val category: String = "",
    val imageUrl: String = "",
    val description: String? = null
)