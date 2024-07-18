package com.example.empty3.data

import com.example.empty3.api.*
import com.varabyte.kobweb.browser.api
import io.ktor.utils.io.core.*
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


suspend fun addProduct(product: Product): String {
    return window.api.tryPost(
        apiPath = "addproduct",
        body = Json.encodeToString(product).encodeToByteArray()
    )?.decodeToString().toString()
}

suspend fun getAllSubscriber(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(apiPath = "getallproducts")?.decodeToString()
        if (result != null) {
            onSuccess(Json.decodeFromString(result))
        } else {
            onError(Exception("Something went wrong"))
        }
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun getAllFoods(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(apiPath = "getallfoods")?.decodeToString()
        if (result != null) {
            onSuccess(Json.decodeFromString(result))
        } else {
            onError(Exception("Something went wrong"))
        }
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun getAllDrinks(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(apiPath = "getalldrinks")?.decodeToString()
        if (result != null) {
            onSuccess(Json.decodeFromString(result))
        } else {
            onError(Exception("Something went wrong"))
        }
    } catch (e: Exception) {
        println(e)
        onError(e)
    }
}

suspend fun getAllColas(
    onSuccess: (ApiListResponse) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        val result = window.api.tryGet(apiPath = "getallcolas")?.decodeToString()
        if (result != null) {
            val apiResponse = json.decodeFromString(ListResponseSerializer, result)
            onSuccess(apiResponse)
        } else {
            onError(Exception("Failed to retrieve data"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}


suspend fun getAllProducts(onSuccess: (ApiListResponse) -> Unit, onError: (Exception) -> Unit) {
    try {
        val result = window.api.tryGet(apiPath = "getallproducts")?.decodeToString()
        if (result != null) {
            val apiResponse = json.decodeFromString(ListResponseSerializer, result)
            onSuccess(apiResponse)
        } else {
            onError(Exception("Failed to retrieve data"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}


suspend fun addReview(productId: String, username: String, rating: Int, comment: String, onSuccess: (ApiListResponse) -> Unit, onError: (Exception) -> Unit) {
    try {
        val reviewRequest = ReviewRequest(productId, username, rating, comment)
        val response = window.api.tryPost(
            apiPath = "addreview",
            body = Json.encodeToString(reviewRequest).toByteArray()
        )?.decodeToString()
        response?.let {
            val apiResponse = Json.decodeFromString(ApiListResponse.serializer(), it)
            if (apiResponse is ApiListResponse.SuccessProducts) {
                onSuccess(apiResponse)
            } else if (apiResponse is ApiListResponse.Error) {
                onError(Exception(apiResponse.message))
            }
        } ?: run {
            onError(Exception("Failed to add review"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}





suspend fun updateProduct(product: Product, onSuccess: (ApiListResponse) -> Unit, onError: (Exception) -> Unit) {
    try {
        val response = window.api.tryPost(
            apiPath = "updateproduct",
            body = Json.encodeToString(product).toByteArray()
        )?.decodeToString()
        response?.let {
            val apiResponse = Json.decodeFromString(ApiListResponse.serializer(), it)
            onSuccess(apiResponse)
        } ?: run {
            onError(Exception("Failed to update product"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun registerUser(user: User, onSuccess: (ApiListResponse) -> Unit, onError: (Exception) -> Unit) {
    try {
        val response = window.api.tryPost(
            apiPath = "register",
            body = Json.encodeToString(user).toByteArray()
        )?.decodeToString()
        response?.let {
            val apiResponse = Json.decodeFromString(ListResponseSerializer, it)
            onSuccess(apiResponse)
        } ?: run {
            onError(Exception("Failed to register"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun loginUser(credentials: UserCredentials, onSuccess: (ApiListResponse) -> Unit, onError: (Exception) -> Unit) {
    try {
        val response = window.api.tryPost(
            apiPath = "login",
            body = Json.encodeToString(credentials).toByteArray()
        )?.decodeToString()
        response?.let {
            val apiResponse = Json.decodeFromString(ListResponseSerializer, it)
            onSuccess(apiResponse)
        } ?: run {
            onError(Exception("Failed to log in"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}

suspend fun deleteReview(productId: String, username: String, onSuccess: (ApiListResponse) -> Unit, onError: (Exception) -> Unit) {
    try {
        val reviewDeleteRequest = ReviewDeleteRequest(productId, username)
        val response = window.api.tryPost(
            apiPath = "deletereview",
            body = Json.encodeToString(reviewDeleteRequest).toByteArray()
        )?.decodeToString()
        response?.let {
            val apiResponse = Json.decodeFromString(ApiListResponse.serializer(), it)
            if (apiResponse is ApiListResponse.SuccessProducts) {
                onSuccess(apiResponse)
            } else if (apiResponse is ApiListResponse.Error) {
                onError(Exception(apiResponse.message))
            }
        } ?: run {
            onError(Exception("Failed to delete review"))
        }
    } catch (e: Exception) {
        onError(e)
    }
}

















