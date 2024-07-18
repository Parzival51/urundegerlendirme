package com.example.empty3.api

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
sealed class ApiListResponse {
    @Serializable
    @SerialName("loading")
    object Loading : ApiListResponse()

    @Serializable
    @SerialName("successProducts")
    data class SuccessProducts(val data: List<Product>) : ApiListResponse()

    @Serializable
    @SerialName("successUser")
    data class SuccessUser(val data: User) : ApiListResponse()

    @Serializable
    @SerialName("error")
    data class Error(val message: String, val errorCode: Int? = null) : ApiListResponse()
}


object ListResponseSerializer : JsonContentPolymorphicSerializer<ApiListResponse>(ApiListResponse::class) {
    override fun selectDeserializer(element: JsonElement): KSerializer<out ApiListResponse> {
        val jsonObject = element.jsonObject
        return when {
            "data" in jsonObject -> {
                val dataElement = jsonObject["data"]
                if (dataElement is JsonObject) {
                    ApiListResponse.SuccessUser.serializer()
                } else {
                    ApiListResponse.SuccessProducts.serializer()
                }
            }
            "message" in jsonObject -> ApiListResponse.Error.serializer()
            else -> ApiListResponse.Loading.serializer()
        }
    }
}

@Serializable
data class ReviewRequest(val productId: String, val username: String, val rating: Int, val comment: String)

@Serializable
data class UserCredentials(val usernameOrEmail: String, val password: String)

@Serializable
data class ReviewDeleteRequest(val productId: String, val username: String)

