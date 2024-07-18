package com.example.empty3.pages.product

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.Product
import com.example.empty3.components.HeaderLayout
import com.example.empty3.data.deleteReview
import com.example.empty3.data.getAllProducts
import com.example.empty3.navigation.Screen
import com.example.empty3.userState.rememberUserState
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Text

@Page("/user/reviews")
@Composable
fun UserReviewsPage() {
    val context = rememberPageContext()
    val userState = rememberUserState()
    val coroutineScope = rememberCoroutineScope()
    var reviews by remember { mutableStateOf<List<Product>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var expandedComments = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            getAllProducts(
                onSuccess = { apiResponse ->
                    if (apiResponse is ApiListResponse.SuccessProducts) {
                        reviews = apiResponse.data.filter { product ->
                            product.comments.any { comment ->
                                comment.username == userState.username
                            }
                        }
                    } else if (apiResponse is ApiListResponse.Error) {
                        errorMessage = apiResponse.message
                    }
                },
                onError = { error ->
                    errorMessage = error.message ?: "Bir hata oluştu"
                }
            )
        }
    }

    HeaderLayout(context = context, userState = userState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .backgroundColor(Color("#F8F9FA"))
                .padding(24.px)
        ) {
            SpanText(
                text = "Değerlendirmelerim",
                modifier = Modifier
                    .fontSize(50.px)
                    .margin(top = 25.px, bottom = 25.px)
                    .color(Color("#212529"))
                    .fontFamily("Montserrat")
                    .fontWeight(FontWeight.Bold)
                    .align(Alignment.CenterHorizontally)
            )

            when {
                errorMessage != null -> {
                    SpanText("Hata: $errorMessage", modifier = Modifier.padding(16.px).fontSize(20.px).color(Color("#DC3545")))
                }
                reviews.isEmpty() -> {
                    Text("Henüz bir değerlendirme yapılmamış.")
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .backgroundColor(Color("#F8F9FA"))
                    ) {
                        reviews.chunked(2).forEach { reviewPair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.px),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                reviewPair.forEach { product ->
                                    product.comments.filter { comment ->
                                        comment.username == userState.username
                                    }.forEach { comment ->
                                        val isExpanded = expandedComments[comment.comment] ?: false
                                        Box(
                                            modifier = Modifier
                                                .width(48.percent)
                                                .padding(8.px)
                                                .backgroundColor(Colors.White)
                                                .border(1.px, LineStyle.Solid, Color("#E0E0E0"))
                                                .borderRadius(8.px)
                                                .cursor(Cursor.Pointer)
                                                .onClick {
                                                    context.router.navigateTo(Screen.ProductDetailPage(product.id).route)
                                                }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.px)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    SpanText(
                                                        text = product.name,
                                                        modifier = Modifier
                                                            .fontSize(24.px)
                                                            .fontWeight(FontWeight.Bold)
                                                            .color(Color("#212529"))
                                                            .fontFamily("Montserrat")
                                                    )
                                                    SpanText(
                                                        text = "✖",
                                                        modifier = Modifier
                                                            .fontSize(24.px)
                                                            .color(Color("#FF0000"))
                                                            .cursor(Cursor.Pointer)
                                                            .onClick {
                                                                if (window.confirm("Bu yorumu silmek istediğinizden emin misiniz?")) {
                                                                    coroutineScope.launch {
                                                                        deleteReview(product.id, userState.username!!, onSuccess = {
                                                                            reviews = reviews.map { p ->
                                                                                if (p.id == product.id) {
                                                                                    p.copy(
                                                                                        comments = p.comments.filterNot { it.username == userState.username }
                                                                                    )
                                                                                } else {
                                                                                    p
                                                                                }
                                                                            }
                                                                        }, onError = { error ->
                                                                            errorMessage = error.message ?: "Bir hata oluştu"
                                                                        })
                                                                    }
                                                                }
                                                            }
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Start,
                                                    modifier = Modifier.margin(top = 8.px)
                                                ) {
                                                    StarRating(rating = comment.rating) {}
                                                }
                                                SpanText(
                                                    text = if (isExpanded) comment.comment else comment.comment.take(100),
                                                    modifier = Modifier
                                                        .fontSize(16.px)
                                                        .color(Color("#6C757D"))
                                                        .fontFamily("Montserrat")
                                                        .margin(top = 8.px)
                                                        .width(100.percent)
                                                        .styleModifier {
                                                            property("overflow", "hidden")
                                                            property("text-overflow", "ellipsis")
                                                            property("white-space", "pre-wrap")
                                                            property("word-wrap", "break-word")
                                                            property("word-break", "break-word")
                                                            property("max-height", if (isExpanded) "none" else "4.5em")
                                                            property("line-height", "1.5em")
                                                        }
                                                )
                                                if (!isExpanded && comment.comment.length > 100) {
                                                    SpanText(
                                                        text = "Devamını göster",
                                                        modifier = Modifier
                                                            .color(Colors.Blue)
                                                            .fontSize(14.px)
                                                            .cursor(Cursor.Pointer)
                                                            .styleModifier {
                                                                property("display", "block")
                                                                property("text-align", "right")
                                                            }
                                                            .margin(top = 8.px)
                                                            .onClick {
                                                                expandedComments[comment.comment] = true
                                                            }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}








