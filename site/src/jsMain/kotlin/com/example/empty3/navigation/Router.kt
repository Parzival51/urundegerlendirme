package com.example.empty3.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.core.init.initKobweb
import com.varabyte.kobweb.navigation.RoutePrefix
import com.varabyte.kobweb.navigation.Router
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable


val LocalRouter = staticCompositionLocalOf<Router> { error("No Router provided") }

@Composable
fun ErrorPage(message: String) {
    Text("Error: $message")
}

fun main() {
    RoutePrefix.set("")
    val router = Router()
    initKobweb(router) { ctx ->
        ctx.router.register("/") { com.example.empty3.pages.HomePage() }
        ctx.router.register("/product/{id}") { com.example.empty3.pages.product.ProductDetailPage() }
        ctx.router.register("/updateProduct") { com.example.empty3.pages.product.ProductUpdatePage() }
        ctx.router.register("/category/{category}") { com.example.empty3.pages.colas.CategoryPage() }
        ctx.router.register("/register") { com.example.empty3.pages.registerandlogin.RegisterPage() }
        ctx.router.register("/login") { com.example.empty3.pages.registerandlogin.LoginPage() }
        ctx.router.register("/search/{query}") { com.example.empty3.pages.product.SearchResultPage() }
        ctx.router.register("/user/reviews") { com.example.empty3.pages.product.UserReviewsPage() }
    }

    renderComposable(rootElementId = "root") {
        CompositionLocalProvider(LocalRouter provides router) {
            App()
        }
    }
}


