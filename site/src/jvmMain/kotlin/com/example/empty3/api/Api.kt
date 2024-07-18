package com.example.empty3.api

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.empty3.api.data.MongoDbImpl
import com.varabyte.kobweb.api.Api
import com.varabyte.kobweb.api.ApiContext
import com.varabyte.kobweb.api.data.getValue
import com.varabyte.kobweb.api.http.setBodyText
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId


@Api(routeOverride = "addproduct")
suspend fun addProduct(context: ApiContext) {
    try {
        val product = context.req.body?.decodeToString()?.let { Json.decodeFromString<Product>(it) }
        val newProduct = product?.copy(id = ObjectId().toString())
        val responseBody = Json.encodeToString(newProduct?.let { context.data.getValue<MongoDbImpl>().addProduct(it) })
        context.res.setBodyText(responseBody)
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(e))
    }
}

@Api(routeOverride = "getallproducts")
suspend fun getAllProducts(context: ApiContext) {
    try {
        val products = context.data.getValue<MongoDbImpl>().getAllProducts()
        context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(data = products)))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(message = e.message.toString())))
    }
}


@Api(routeOverride = "getallfoods")
suspend fun getAllFoods(context: ApiContext) {
    try {
        val foods = context.data.getValue<MongoDbImpl>().getAllFoods()
        context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(data = foods)))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(message = e.message.toString())))
    }
}


@Api(routeOverride = "getalldrinks")
suspend fun getAllDrinks(context: ApiContext) {
    try {
        val drinks = context.data.getValue<MongoDbImpl>().getAllDrinks()
        context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(data = drinks)))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(message = e.message.toString())))
    }
}


@Api(routeOverride = "getallcolas")
suspend fun getAllColas(context: ApiContext) {
    try {
        val colas = context.data.getValue<MongoDbImpl>().getAllColas()
        context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(data = colas)))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(message = e.message.toString())))
    }
}


@Api(routeOverride = "addreview")
suspend fun addReview(context: ApiContext) {
    try {
        val request = context.req.body?.decodeToString()?.let { Json.decodeFromString<ReviewRequest>(it) }
        request?.let {
            val success = context.data.getValue<MongoDbImpl>().addReview(it.productId, it.username, it.rating, it.comment)
            if (success) {
                val updatedProduct = context.data.getValue<MongoDbImpl>().getProductById(it.productId)
                if (updatedProduct != null) {
                    context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(listOf(updatedProduct))))
                } else {
                    context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Product not found after update")))
                }
            } else {
                context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Failed to update comments and rating", errorCode = 1001)))
            }
        }
    } catch (e: IllegalArgumentException) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(e.message ?: "Kullanıcı daha önce bu ürüne yorum yapmış", errorCode = 1001)))
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(e.message ?: "Unknown error")))
    }
}


@Api(routeOverride = "updateproduct")
suspend fun updateProduct(context: ApiContext) {
    try {
        val product = context.req.body?.decodeToString()?.let { Json.decodeFromString<Product>(it) }
        val success = product?.let { context.data.getValue<MongoDbImpl>().updateProduct(it) } ?: false
        if (success && product != null) {
            context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(data = listOf(product))))
        } else {
            context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Failed to update product")))
        }
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(e.message ?: "Unknown error")))
    }
}



@Api(routeOverride = "register")
suspend fun registerUser(context: ApiContext) {
    try {
        val user = context.req.body?.decodeToString()?.let { Json.decodeFromString<User>(it) }
        val hashedUser = user?.copy(password = BCrypt.withDefaults().hashToString(12, user.password.toCharArray()))
        hashedUser?.let {
            context.data.getValue<MongoDbImpl>().addUser(it)
            context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessUser(it)))
        }
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(e.message ?: "Unknown error")))
    }
}

@Api(routeOverride = "login")
suspend fun loginUser(context: ApiContext) {
    try {
        val credentials = context.req.body?.decodeToString()?.let { Json.decodeFromString<UserCredentials>(it) }
        credentials?.let {
            val user = context.data.getValue<MongoDbImpl>().authenticateUser(it.usernameOrEmail, it.password)
            if (user != null) {
                context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessUser(user)))
            } else {
                context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Geçersiz kullanıcı adı veya şifre")))
            }
        } ?: run {
            context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Geçersiz giriş bilgileri")))
        }
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(e.message ?: "Bilinmeyen hata")))
    }
}



@Api(routeOverride = "deletereview")
suspend fun deleteReview(context: ApiContext) {
    try {
        val request = context.req.body?.decodeToString()?.let { Json.decodeFromString<ReviewDeleteRequest>(it) }
        request?.let {
            val success = context.data.getValue<MongoDbImpl>().deleteReview(it.productId, it.username)
            if (success) {
                val updatedProduct = context.data.getValue<MongoDbImpl>().getProductById(it.productId)
                if (updatedProduct != null) {
                    context.res.setBodyText(Json.encodeToString(ApiListResponse.SuccessProducts(listOf(updatedProduct))))
                } else {
                    context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Product not found after review deletion")))
                }
            } else {
                context.res.setBodyText(Json.encodeToString(ApiListResponse.Error("Failed to delete review", errorCode = 1002)))
            }
        }
    } catch (e: Exception) {
        context.res.setBodyText(Json.encodeToString(ApiListResponse.Error(e.message ?: "Unknown error")))
    }
}


















