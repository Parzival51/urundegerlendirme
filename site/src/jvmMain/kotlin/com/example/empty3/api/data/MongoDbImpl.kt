package com.example.empty3.api.data

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.empty3.api.Comment
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.varabyte.kobweb.api.data.add
import com.varabyte.kobweb.api.init.InitApi
import com.varabyte.kobweb.api.init.InitApiContext
import kotlinx.coroutines.flow.toList
import com.example.empty3.api.Product
import com.example.empty3.api.User
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.Updates.*
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document


@InitApi
fun initializeMongo(context: InitApiContext) {
    System.setProperty(
        "org.litote.mongo.test.mapping.service",
        "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
    )
    context.data.add(MongoDbImpl())
}

class MongoDbImpl : MongoDb {

    private val connectionString = "mongodb+srv://yusufemreai51:l82rL2r8TuyARnvA@productrating.wpjwteh.mongodb.net/?retryWrites=true&w=majority&appName=productRating"
    private val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()
    private val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(connectionString))
        .serverApi(serverApi)
        .build()

    private val client = MongoClient.create(mongoClientSettings)
    private val database: MongoDatabase = client.getDatabase("productrating")
    private val productCollection = database.getCollection<Product>("products")
    private val userDatabase: MongoDatabase = client.getDatabase("userdatabase")
    private val userCollection = userDatabase.getCollection<User>("users")

    override suspend fun getAllProducts(): List<Product> {
        return productCollection.find().toList()
    }

    override suspend fun getAllFoods(): List<Product> {
        return productCollection.find(eq("category", "Food")).toList()
    }

    override suspend fun getAllDrinks(): List<Product> {
        return productCollection.find(eq("category", "Drink")).toList()
    }

    override suspend fun addProduct(product: Product): Boolean {
        val productWithDefaults = product.copy(
            commentCount = product.commentCount,
            reviewCount = product.reviewCount,
            description = product.description ?: ""
        )
        return productCollection.insertOne(productWithDefaults).wasAcknowledged()
    }

    override suspend fun getAllColas(): List<Product> {
        return productCollection.find(eq("category", "Cola")).toList()
    }

    override suspend fun getProductById(productId: String): Product? {
        return productCollection.find(eq("id", productId)).firstOrNull()
    }

    override suspend fun updateProduct(product: Product): Boolean {
        val filter = eq("id", product.id)
        val updates = combine(
            set("name", product.name),
            set("price", product.price),
            set("rating", product.rating),
            set("comments", product.comments),
            set("commentCount", product.commentCount),
            set("reviewCount", product.reviewCount),
            set("category", product.category),
            set("imageUrl", product.imageUrl),
            set("description", product.description)
        )
        val result = productCollection.updateOne(filter, updates)
        return result.wasAcknowledged()
    }

    override suspend fun addReview(productId: String, username: String, rating: Int, comment: String): Boolean {
        val product = getProductById(productId)
        return product?.let {
            if (it.comments.any { review -> review.username == username }) {
                throw IllegalArgumentException("Kullanıcı daha önce bu ürüne yorum yapmış")
            }

            val updatedComments = it.comments + Comment(username, rating, comment)
            val totalRating = it.rating * it.reviewCount + rating
            val newReviewCount = it.reviewCount + 1
            val updatedRating = totalRating / newReviewCount
            val updatedProduct = it.copy(
                comments = updatedComments,
                commentCount = updatedComments.size,
                reviewCount = newReviewCount,
                rating = updatedRating
            )
            updateProduct(updatedProduct)
        } ?: false
    }



    override suspend fun addUser(user: User): Boolean {
        return userCollection.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserByUsernameOrEmail(usernameOrEmail: String): User?{
        return userCollection.find(
            or(
                eq("username", usernameOrEmail),
                eq("email", usernameOrEmail)
            )
        ).firstOrNull()
    }

    override suspend fun authenticateUser(usernameOrEmail: String, password: String): User? {
        val user = getUserByUsernameOrEmail(usernameOrEmail)
        user?.let {
            if (BCrypt.verifyer().verify(password.toCharArray(), it.password).verified) {
                return user
            }
        }
        return null
    }

    override suspend fun deleteReview(productId: String, username: String): Boolean {
        val product = getProductById(productId)
        return product?.let {
            val updatedComments = it.comments.filterNot { review -> review.username == username }
            val reviewToRemove = it.comments.find { review -> review.username == username }
            val totalRating = it.rating * it.reviewCount - (reviewToRemove?.rating ?: 0)
            val newReviewCount = it.reviewCount - 1
            val updatedRating = if (newReviewCount > 0) totalRating / newReviewCount else 0

            val updatedProduct = it.copy(
                comments = updatedComments,
                commentCount = updatedComments.size,
                reviewCount = newReviewCount,
                rating = updatedRating
            )
            updateProduct(updatedProduct)
        } ?: false
    }
    init {
        runBlocking {
            database.runCommand(Document("ping", 1))
            println("Pinged your deployment. You successfully connected to MongoDB!")
        }
    }

}
