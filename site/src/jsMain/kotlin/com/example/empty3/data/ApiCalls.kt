package com.example.empty3.data

sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val exception: Exception) : ApiResult<T>()
}

private suspend inline fun <T> handleApiCall(
    apiPath: String,
    method: String,
    body: ByteArray? = null,
    crossinline deserialize: (String) -> T
): ApiResult<T> {
    return try {
        val response = when (method) {
            "GET" -> window.api.tryGet(apiPath)
            "POST" -> window.api.tryPost(apiPath, body)
            else -> throw IllegalArgumentException("Unsupported HTTP method: $method")
        }
        val decodedResponse = response?.decodeToString()
        if (decodedResponse != null) {
            ApiResult.Success(deserialize(decodedResponse))
        } else {
            ApiResult.Error(Exception("API call failed or returned null/empty response"))
        }
    } catch (e: Exception) {
        ApiResult.Error(e)
    }
}

suspend inline fun <reified T> makeGetRequest(
    apiPath: String,
    noinline deserialize: (String) -> T
): ApiResult<T> {
    return handleApiCall(apiPath, "GET", deserialize = deserialize)
}

suspend inline fun <reified Request, reified Response> makePostRequest(
    apiPath: String,
    requestBody: Request,
    noinline deserialize: (String) -> Response,
    noinline serialize: (Request) -> String
): ApiResult<Response> {
    val serializedBody = serialize(requestBody).encodeToByteArray()
    return handleApiCall(apiPath, "POST", serializedBody, deserialize = deserialize)
}

import com.example.empty3.api.*
import com.varabyte.kobweb.browser.api
import io.ktor.utils.io.core.*
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


suspend fun addProduct(product: Product): ApiResult<String> {
    return makePostRequest(
        apiPath = "addproduct",
        requestBody = product,
        serialize = { Json.encodeToString(it) },
        deserialize = { it }
    )
}

suspend fun getAllFoods(): ApiResult<ApiListResponse> {
    return makeGetRequest(
        apiPath = "getallfoods",
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun getAllDrinks(): ApiResult<ApiListResponse> {
    return makeGetRequest(
        apiPath = "getalldrinks",
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun getAllColas(): ApiResult<ApiListResponse> {
    return makeGetRequest(
        apiPath = "getallcolas",
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun getAllProducts(): ApiResult<ApiListResponse> {
    return makeGetRequest(
        apiPath = "getallproducts",
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun addReview(productId: String, username: String, rating: Int, comment: String): ApiResult<ApiListResponse> {
    val reviewRequest = ReviewRequest(productId, username, rating, comment)
    return makePostRequest(
        apiPath = "addreview",
        requestBody = reviewRequest,
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun updateProduct(product: Product): ApiResult<ApiListResponse> {
    return makePostRequest(
        apiPath = "updateproduct",
        requestBody = product,
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun registerUser(user: User): ApiResult<ApiListResponse> {
    return makePostRequest(
        apiPath = "register",
        requestBody = user,
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun loginUser(credentials: UserCredentials): ApiResult<ApiListResponse> {
    return makePostRequest(
        apiPath = "login",
        requestBody = credentials,
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

suspend fun deleteReview(productId: String, username: String): ApiResult<ApiListResponse> {
    val reviewDeleteRequest = ReviewDeleteRequest(productId, username)
    return makePostRequest(
        apiPath = "deletereview",
        requestBody = reviewDeleteRequest,
        serialize = { Json.encodeToString(it) },
        deserialize = { Json.decodeFromString<ApiListResponse>(it) }
    )
}

/*
Refactoring Summary:
- Centralized API call logic using generic `makeGetRequest` and `makePostRequest` functions.
- Standardized error handling and response wrapping using the `ApiResult` sealed class.
- Eliminated redundant code for try-catch blocks and JSON (de)serialization.
- Standardized function names and signatures for improved clarity and maintainability.
- Removed duplicate function `fetchProductsAlternate`.
*/
