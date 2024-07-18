package com.example.empty3.api

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val json = Json {
    serializersModule = SerializersModule {
        polymorphic(ApiListResponse::class) {
            subclass(ApiListResponse.Loading::class, ApiListResponse.Loading.serializer())
            subclass(ApiListResponse.SuccessProducts::class, ApiListResponse.SuccessProducts.serializer())
            subclass(ApiListResponse.SuccessUser::class, ApiListResponse.SuccessUser.serializer())
            subclass(ApiListResponse.Error::class, ApiListResponse.Error.serializer())
        }
    }
    ignoreUnknownKeys = true
    coerceInputValues = true
}

