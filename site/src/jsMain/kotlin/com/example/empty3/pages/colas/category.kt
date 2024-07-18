package com.example.empty3.pages.colas

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.Product
import com.example.empty3.components.HeaderLayout
import com.example.empty3.data.getAllProducts
import com.example.empty3.navigation.ErrorPage
import com.example.empty3.navigation.Screen
import com.example.empty3.userState.rememberUserState
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.Svg
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.launch
import com.varabyte.kobweb.core.PageContext
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.dom.CheckboxInput
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


@Page("/category/{category}")
@Composable
fun CategoryPage() {
    val context = rememberPageContext()
    val category = context.route.params["category"] ?: return ErrorPage("Kategori bulunamadı")
    var response by remember { mutableStateOf<ApiListResponse>(ApiListResponse.Loading) }
    val coroutineScope = rememberCoroutineScope()
    val userState = rememberUserState()
    var selectedFilter by remember { mutableStateOf("En Yüksek Yıldız") }
    var expandedComments = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(category) {
        coroutineScope.launch {
            getAllProducts(
                onSuccess = { apiResponse ->
                    if (apiResponse is ApiListResponse.SuccessProducts) {
                        response = ApiListResponse.SuccessProducts(apiResponse.data.filter { it.category == category })
                    } else if (apiResponse is ApiListResponse.Error) {
                        response = ApiListResponse.Error(apiResponse.message)
                    }
                },
                onError = {
                    response = ApiListResponse.Error(it.message ?: "An error occurred")
                }
            )
        }
    }

    fun sortProducts(products: List<Product>): List<Product> {
        return when (selectedFilter) {
            "En Çok Yorum" -> products.sortedByDescending { it.commentCount }
            "En Yüksek Yıldız" -> products.sortedWith(compareByDescending<Product> { it.rating }
                .thenByDescending { it.reviewCount })
            "En Düşük Yıldız" -> products.sortedWith(compareBy<Product> { it.rating }
                .thenByDescending { it.reviewCount })
            else -> products
        }
    }

    HeaderLayout(context = context, userState = userState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .backgroundColor(Color("#F8F9FA"))
                .padding(24.px)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SpanText(
                        text = category,
                        modifier = Modifier
                            .fontSize(50.px)
                            .margin(top = 25.px, bottom = 25.px)
                            .color(Color("#212529"))
                            .fontFamily("Montserrat")
                            .fontWeight(FontWeight.Bold)
                            .align(Alignment.CenterHorizontally)
                    )

                    when (response) {
                        is ApiListResponse.Loading -> {
                            Text("Yükleniyor...")
                        }
                        is ApiListResponse.SuccessProducts -> {
                            val products = sortProducts((response as ApiListResponse.SuccessProducts).data)
                            if (products.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .backgroundColor(Color("#F8F9FA"))
                                ) {
                                    products.forEach { product ->
                                        ProductCard(product, context, expandedComments)
                                        Div(
                                            attrs = {
                                                style {
                                                    height(16.px) // Ürün kartları arasına boşluk eklemek için Div
                                                }
                                            }
                                        )
                                    }
                                }
                            } else {
                                SpanText("$category kategorisinde ürün bulunmamaktadır.", modifier = Modifier.align(Alignment.CenterHorizontally).margin(top = 16.px))
                            }
                        }
                        is ApiListResponse.SuccessUser -> {
                            // SuccessUser durumu için bir işlem yapmamız gerekmiyor
                        }
                        is ApiListResponse.Error -> {
                            SpanText("Ürünler yüklenirken bir hata oluştu: ${(response as ApiListResponse.Error).message}", modifier = Modifier.align(Alignment.CenterHorizontally).margin(top = 16.px))
                        }
                    }
                }

                // Filtreleme Alanı
                Column(
                    modifier = Modifier
                        .margin(top = 110.px, leftRight = 150.px)
                        .align(Alignment.Top)
                ) {
                    SpanText(
                        text = "Filtreleme",
                        modifier = Modifier
                            .fontSize(24.px)
                            .fontWeight(FontWeight.Bold)
                            .color(Color("#212529"))
                            .fontFamily("Montserrat")
                            .margin(bottom = 24.px)
                    )
                    Div(
                        attrs = Modifier.padding(bottom = 16.px).toAttrs {
                            onClick { selectedFilter = "En Çok Yorum" }
                        }
                    ) {
                        CheckboxInput(checked = selectedFilter == "En Çok Yorum")
                        SpanText(
                            text = "En Çok Yorum",
                            modifier = Modifier.fontSize(18.px).margin(left = 8.px)
                        )
                    }
                    Div(
                        attrs = Modifier.padding(bottom = 16.px).toAttrs {
                            onClick { selectedFilter = "En Yüksek Yıldız" }
                        }
                    ) {
                        CheckboxInput(checked = selectedFilter == "En Yüksek Yıldız")
                        SpanText(
                            text = "En Yüksek Yıldız",
                            modifier = Modifier.fontSize(18.px).margin(left = 8.px)
                        )
                    }
                    Div(
                        attrs = Modifier.padding(bottom = 16.px).toAttrs {
                            onClick { selectedFilter = "En Düşük Yıldız" }
                        }
                    ) {
                        CheckboxInput(checked = selectedFilter == "En Düşük Yıldız")
                        SpanText(
                            text = "En Düşük Yıldız",
                            modifier = Modifier.fontSize(18.px).margin(left = 8.px)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, context: PageContext, expandedComments: MutableMap<String, Boolean>) {
    val isExpanded = expandedComments[product.comments.firstOrNull()?.comment ?: ""] ?: false
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.px)
            .margin(left = 75.px)
            .backgroundColor(Colors.White)
            .border(1.px, LineStyle.Solid, Color("#E0E0E0"))
            .borderRadius(8.px)
            .cursor(Cursor.Pointer)
            .onClick {
                context.router.navigateTo(Screen.ProductDetailPage(product.id).route)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Boşluk bırakan div
            Div(
                attrs = {
                    style {
                        width(50.px)
                    }
                }
            )

            // Resim
            Image(
                src = product.imageUrl,
                alt = "${product.name} resmi",
                modifier = Modifier.size(150.px).borderRadius(8.px)
            )

            // Ürün Detayları
            Column(
                modifier = Modifier
                    .padding(left = 16.px)
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                SpanText(
                    text = product.name,
                    modifier = Modifier
                        .padding(left = 4.px)
                        .fontSize(24.px)
                        .fontWeight(FontWeight.Bold)
                        .color(Color("#212529"))
                        .fontFamily("Montserrat")
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.margin(top = 8.px)
                ) {
                    StarRating(rating = product.rating.toInt()) {}
                    SpanText(
                        text = "${product.reviewCount} Değerlendirme",
                        modifier = Modifier.fontSize(16.px).margin(left = 8.px)
                            .color(Color("#212529"))
                            .fontFamily("Montserrat")
                    )
                }
            }

            Div(
                attrs = {
                    style {
                        width(100.px)
                    }
                }
            )

            Box(
                modifier = Modifier
                    .padding(left = 16.px)
                    .backgroundColor(Color("#F8F9FA"))
                    .border(1.px, LineStyle.Solid, Color("#E0E0E0"))
                    .borderRadius(8.px)
                    .padding(16.px)
                    .width(350.px) // Enini biraz büyüttük
                    .align(Alignment.CenterVertically)
            ) {
                Column {
                    SpanText(
                        text = product.comments.firstOrNull()?.username ?: "Anonim",
                        modifier = Modifier
                            .fontSize(14.px)
                            .fontWeight(FontWeight.Bold)
                            .color(Color("#007BFF"))
                            .fontFamily("Montserrat")
                            .margin(bottom = 4.px)
                    )
                    val comment = product.comments.firstOrNull()?.comment ?: "Henüz yorum yapılmamış."
                    val isExpanded = expandedComments[comment] ?: false
                    SpanText(
                        text = if (isExpanded) comment else comment.take(50),
                        modifier = Modifier
                            .fontSize(16.px)
                            .color(Color("#6C757D"))
                            .fontFamily("Montserrat")
                            .margin(top = 8.px)
                            .styleModifier {
                                property("overflow", "hidden")
                                property("text-overflow", "ellipsis")
                                property("white-space", "pre-wrap") // Proper word wrapping
                                property("word-wrap", "break-word") // Adds proper word breaking
                                property("word-break", "break-word") // Adds proper word breaking
                            }
                    )
                    if (!isExpanded && comment.length > 50) {
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
                                    expandedComments[comment] = true
                                }
                        )
                    }
                }
            }


        }
    }
}






@Composable
fun StarRating(rating: Int, onRatingChange: (Int) -> Unit) {
    Row {
        (1..5).forEach { star ->
            SvgStar(
                filled = rating >= star,
                onClick = { onRatingChange(star) }
            )
        }
    }
}

@Composable
fun SvgStar(filled: Boolean, onClick: () -> Unit) {
    val starPath = if (filled) {
        "M12 .587l3.668 7.431L24 9.748l-6 5.845L19.335 24 12 19.412 4.665 24 6 15.593 0 9.748l8.332-1.73z"
    } else {
        "M12 1.587l3.668 7.431 8.332 1.73-6 5.845 1.335 8.412-7.335-4.588L4.665 24 6 15.593 0 9.748l8.332-1.73z"
    }

    Svg(
        attrs = {
            onClick { onClick() }
            style {
                width(24.px)
                height(24.px)
                cursor("pointer")
                margin(4.px)
            }
        }
    ) {
        Path(
            attrs = {
                d(starPath)
                attr("fill", if (filled) "#FFD700" else "#DDDDDD")
            }
        )
    }
}






