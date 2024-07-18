package com.example.empty3.pages

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.Product
import com.example.empty3.components.HeaderLayout
import com.example.empty3.navigation.Screen
import com.example.empty3.userState.rememberUserState
import com.example.empty3.data.addProduct
import com.example.empty3.data.getAllProducts
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.selectors.hover
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input

@InitSilk
fun initStyles(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        cssLayers.add("initStyles")
        layer("initStyles") {
            registerStyle(".init-style") {
                base {
                    Modifier
                        .backgroundColor(rgb(30, 136, 229))
                        .borderRadius(75.px)
                        .padding(16.px)
                        .cursor(Cursor.Pointer)
                }
                hover {
                    Modifier.backgroundColor(rgb(21, 101, 192))
                }
            }
            registerStyle(".category-box") {
                base {
                    Modifier
                        .backgroundColor(Color.rgb(255, 255, 255))
                        .borderRadius(16.px)
                        .padding(16.px)
                        .cursor(Cursor.Pointer)
                }
                hover {
                    Modifier.styleModifier {
                        property("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.2)")
                    }
                }
            }
        }
    }
}



@Page("/")
@Composable
fun HomePage() {
    val scope = rememberCoroutineScope()
    val context = rememberPageContext()
    var isFormVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0.0) }
    var category by remember { mutableStateOf("") }
    var image by remember { mutableStateOf("") }
    val userState = rememberUserState()

    var categories by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(Unit) {
        scope.launch {
            getAllProducts(
                onSuccess = { response ->
                    if (response is ApiListResponse.SuccessProducts) {
                        categories = response.data
                            .groupBy { it.category }
                            .mapValues { "" } // Resimleri şimdilik boş bırakıyoruz
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

    HeaderLayout(context = context, userState = userState) {
        Column(
            modifier = Modifier.fillMaxSize().backgroundColor(Color.rgb(224, 224, 224))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.px),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SpanText(
                    text = "Ürünleri Değerlendir, Yorumları Oku, En İyi Seçimi Yap!",
                    modifier = Modifier
                        .fontSize(40.px)
                        .margin(top = 35.px)
                        .color(Colors.Black)
                        .fontFamily("Montserrat")
                        .fontWeight(FontWeight.Bold)
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier
                        .margin(top = 30.px)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        attrs = Modifier
                            .classNames("init-style")
                            .height(75.px)
                            .width(300.px)
                            .onClick {
                                if (userState.username.isNullOrEmpty()) {
                                    context.router.navigateTo(Screen.LoginPage.route)
                                } else {
                                    isFormVisible = true
                                }
                            }
                            .toAttrs()
                    ) {
                        SpanText(
                            text = "Ürün Ekle",
                            modifier = Modifier.color(Colors.White).fontSize(16.px)
                        )
                    }

                    Button(
                        attrs = Modifier
                            .classNames("init-style")
                            .height(75.px)
                            .width(300.px)
                            .margin(left = 50.px)
                            .onClick {
                                if (userState.username.isNullOrEmpty()) {
                                    context.router.navigateTo(Screen.LoginPage.route)
                                } else {
                                    context.router.navigateTo(Screen.ProductUpdatePage.route)
                                }
                            }
                            .toAttrs()
                    ) {
                        SpanText(
                            text = "Ürün Güncelle",
                            modifier = Modifier.color(Colors.White).fontSize(16.px)
                        )
                    }
                }
            }

            // Kategoriler
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.px)
                    .backgroundColor(Color.rgb(224, 224, 224))
            ) {
                val categoryList = categories.keys.toList()

                for (i in categoryList.indices step 5) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(leftRight = 64.px) // Ürünleri değerlendir kısmıyla hizalamak için padding ekledik
                            .margin(bottom = 16.px),
                        horizontalArrangement = Arrangement.Start // Soldan başlamak için düzenlendi
                    ) {
                        for (j in 0 until 5) {
                            if (i + j < categoryList.size) {
                                val categoryName = categoryList[i + j]
                                Box(
                                    modifier = Modifier
                                        .classNames("category-box")
                                        .size(200.px)
                                        .margin(right = 16.px) // Aralarına boşluk eklemek için margin kullanıldı
                                        .onClick {
                                            context.router.navigateTo(Screen.CategoryPage(categoryName).route)
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Şimdilik resim yok
                                        Box(
                                            modifier = Modifier
                                                .size(150.px)
                                                .borderRadius(16.px)
                                                .backgroundColor(Color.rgb(200, 200, 200))
                                        )
                                        SpanText(
                                            text = categoryName,
                                            modifier = Modifier.margin(top = 8.px).color(Colors.Black).fontSize(16.px)
                                        )
                                    }
                                }
                            } else {
                                // Boş alanları doldurmak için boş kutucuklar eklenir
                                Box(modifier = Modifier.size(200.px).margin(right = 16.px))
                            }
                        }
                    }
                }
            }

            if (isFormVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .backgroundColor(Color.rgba(0, 0, 0, 0.5f))
                        .display(DisplayStyle.Flex)
                        .justifyContent(JustifyContent.Center)
                        .alignItems(AlignItems.Center)
                        .position(Position.Absolute) // Fixed yerine Absolute yapıldı
                        .top(window.pageYOffset.px) // Mevcut scroll konumuna göre yerleştirildi
                        .left(0.px)
                ) {
                    Box(
                        modifier = Modifier
                            .width(400.px)
                            .backgroundColor(Colors.White)
                            .borderRadius(16.px)
                            .padding(24.px)
                            .styleModifier {
                                property("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.2)")
                            }
                            .onClick { }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.px)
                        ) {
                            // Ürün Adı
                            Input(
                                type = InputType.Text,
                                attrs = Modifier
                                    .width(320.px)
                                    .height(50.px)
                                    .color(Colors.Black)
                                    .backgroundColor(Color.rgb(245, 245, 245))
                                    .padding(12.px)
                                    .fontSize(16.px)
                                    .borderRadius(8.px)
                                    .border(1.px, LineStyle.Solid, Colors.Gray)
                                    .toAttrs {
                                        attr("placeholder", "Product Name")
                                        onInput {
                                            name = it.value
                                        }
                                    }
                            )

                            // Fiyat
                            Input(
                                type = InputType.Number,
                                attrs = Modifier
                                    .width(320.px)
                                    .height(50.px)
                                    .color(Colors.Black)
                                    .backgroundColor(Color.rgb(245, 245, 245))
                                    .padding(12.px)
                                    .fontSize(16.px)
                                    .borderRadius(8.px)
                                    .border(1.px, LineStyle.Solid, Colors.Gray)
                                    .toAttrs {
                                        attr("placeholder", "Price")
                                        onInput {
                                            price = it.value?.toDouble() ?: 0.0
                                        }
                                    }
                            )

                            // Kategori
                            Input(
                                type = InputType.Text,
                                attrs = Modifier
                                    .width(320.px)
                                    .height(50.px)
                                    .color(Colors.Black)
                                    .backgroundColor(Color.rgb(245, 245, 245))
                                    .padding(12.px)
                                    .fontSize(16.px)
                                    .borderRadius(8.px)
                                    .border(1.px, LineStyle.Solid, Colors.Gray)
                                    .toAttrs {
                                        attr("placeholder", "Category")
                                        onInput {
                                            category = it.value
                                        }
                                    }
                            )

                            // Resim URL
                            Input(
                                type = InputType.Text,
                                attrs = Modifier
                                    .width(320.px)
                                    .height(50.px)
                                    .color(Colors.Black)
                                    .backgroundColor(Color.rgb(245, 245, 245))
                                    .padding(12.px)
                                    .fontSize(16.px)
                                    .borderRadius(8.px)
                                    .border(1.px, LineStyle.Solid, Colors.Gray)
                                    .toAttrs {
                                        attr("placeholder", "Image URL")
                                        onInput {
                                            image = it.value
                                        }
                                    }
                            )

                            Row(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .margin(top = 16.px),
                                horizontalArrangement = Arrangement.spacedBy(16.px)
                            ) {
                                Button(
                                    attrs = {
                                        onClick {
                                            scope.launch {
                                                addProduct(
                                                    Product(
                                                        id = "",
                                                        name = name,
                                                        price = price.toLong(),
                                                        category = category,
                                                        imageUrl = image
                                                    )
                                                )
                                                isFormVisible = false
                                            }
                                        }
                                        style {
                                            backgroundColor(Color.rgb(0, 122, 255))
                                            borderRadius(25.px)
                                            padding(16.px)
                                            cursor("pointer")
                                            color(Colors.White)
                                            width(150.px)
                                            property("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)")
                                        }
                                    }
                                ) {
                                    SpanText(text = "Ekle", modifier = Modifier.fontSize(16.px))
                                }

                                Button(
                                    attrs = {
                                        onClick {
                                            isFormVisible = false
                                        }
                                        style {
                                            backgroundColor(Color.rgb(255, 0, 0))
                                            borderRadius(25.px)
                                            padding(16.px)
                                            cursor("pointer")
                                            color(Colors.White)
                                            width(150.px)
                                            property("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)")
                                        }
                                    }
                                ) {
                                    SpanText(text = "İptal", modifier = Modifier.fontSize(16.px))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
