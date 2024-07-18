package com.example.empty3.pages.product

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.Product
import com.example.empty3.data.getAllProducts
import com.example.empty3.data.updateProduct
import com.example.empty3.navigation.Screen
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.margin
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.TextArea
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.navigation.UpdateHistoryMode

@Page("/updateProduct")
@Composable
fun ProductUpdatePage() {
    val context = rememberPageContext()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var filteredProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            getAllProducts(
                onSuccess = { response ->
                    if (response is ApiListResponse.SuccessProducts) {
                        products = response.data.filterNotNull()
                    } else if (response is ApiListResponse.Error) {
                        println("Error fetching products: ${response.message}")
                    }
                },
                onError = { error ->
                    println("Error fetching products: ${error.message}")
                }
            )
        }
    }

    val categories = products.map { it.category }.distinct()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.px),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpanText(
            text = "Ürün Güncelle",
            modifier = Modifier
                .fontSize(36.px)
                .fontWeight(FontWeight.Bold)
                .margin(bottom = 24.px)
        )

        if (selectedCategory == null) {
            // Kategori seçimi
            SpanText(
                text = "Kategori Seçiniz:",
                modifier = Modifier
                    .fontSize(24.px)
                    .margin(bottom = 16.px)
            )
            categories.forEach { category ->
                Button(
                    attrs = {
                        onClick {
                            selectedCategory = category
                            filteredProducts = products.filter { it.category == category }
                        }
                        style {
                            backgroundColor(Color.rgb(245, 245, 245))
                            borderRadius(8.px)
                            padding(16.px)
                            cursor("pointer")
                            color(Colors.Black)
                            margin(bottom = 8.px)
                        }
                    }
                ) {
                    SpanText(
                        text = category,
                        modifier = Modifier.fontSize(16.px)
                    )
                }
            }
        } else if (selectedProduct == null) {
            // Ürün seçimi
            SpanText(
                text = "Ürün Seçiniz:",
                modifier = Modifier
                    .fontSize(24.px)
                    .margin(bottom = 16.px)
            )
            filteredProducts.forEach { product ->
                Button(
                    attrs = {
                        onClick {
                            selectedProduct = product
                        }
                        style {
                            backgroundColor(Color.rgb(245, 245, 245))
                            borderRadius(8.px)
                            padding(16.px)
                            cursor("pointer")
                            color(Colors.Black)
                            margin(bottom = 8.px)
                        }
                    }
                ) {
                    SpanText(
                        text = product.name,
                        modifier = Modifier.fontSize(16.px)
                    )
                }
            }
        } else {
            // Ürün güncelleme formu
            var name by remember { mutableStateOf(selectedProduct!!.name) }
            var price by remember { mutableStateOf(selectedProduct!!.price.toDouble()) }
            var category by remember { mutableStateOf(selectedProduct!!.category) }
            var imageUrl by remember { mutableStateOf(selectedProduct!!.imageUrl) }
            var description by remember { mutableStateOf(selectedProduct!!.description ?: "") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.px)
                    .backgroundColor(Color.rgb(245, 245, 245))
                    .borderRadius(8.px)
                    .padding(16.px)
            ) {
                SpanText(
                    text = "Ürün Güncelleme Formu",
                    modifier = Modifier
                        .fontSize(24.px)
                        .fontWeight(FontWeight.Bold)
                        .margin(bottom = 16.px)
                )

                // Ürün Adı
                Input(
                    type = InputType.Text,
                    attrs = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.px)
                        .toAttrs {
                            value(name)
                            onInput { name = it.value }
                            attr("placeholder", "Ürün Adı")
                        }
                )

                // Fiyat
                Input(
                    type = InputType.Number,
                    attrs = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.px)
                        .toAttrs {
                            value(price.toString())
                            onInput { price = it.value!!.toDouble() }
                            attr("placeholder", "Fiyat")
                        }
                )

                // Kategori
                Input(
                    type = InputType.Text,
                    attrs = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.px)
                        .toAttrs {
                            value(category)
                            onInput { category = it.value }
                            attr("placeholder", "Kategori")
                        }
                )

                // Resim URL
                Input(
                    type = InputType.Text,
                    attrs = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.px)
                        .toAttrs {
                            value(imageUrl)
                            onInput { imageUrl = it.value }
                            attr("placeholder", "Resim URL")
                        }
                )

                // Açıklama
                TextArea(
                    value = description,
                    attrs = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.px)
                        .toAttrs {
                            onInput { description = it.value }
                            attr("placeholder", "Açıklama")
                        }
                )

                Button(
                    attrs = {
                        onClick {
                            scope.launch {
                                val updatedProduct = selectedProduct!!.copy(
                                    name = name,
                                    price = price.toLong(),
                                    category = category,
                                    imageUrl = imageUrl,
                                    description = description
                                )
                                updateProduct(updatedProduct,
                                    onSuccess = {
                                        context.router.navigateTo(Screen.ProductDetailPage(updatedProduct.id).route, UpdateHistoryMode.REPLACE)
                                    },
                                    onError = {
                                        println("Error updating product: ${it.message}")
                                    }
                                )
                            }
                        }
                        style {
                            backgroundColor(Color.rgb(0, 122, 255))
                            borderRadius(8.px)
                            padding(16.px)
                            cursor("pointer")
                            color(Colors.White)
                            width(150.px) // Button width adjusted
                        }
                    }
                ) {
                    SpanText(text = "Güncelle", modifier = Modifier.fontSize(16.px))
                }
            }
        }
    }
}


