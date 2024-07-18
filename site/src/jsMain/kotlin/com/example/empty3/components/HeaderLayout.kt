package com.example.empty3.components

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.Product
import com.example.empty3.api.UserState
import com.example.empty3.pages.product.findSimilarProducts
import com.example.empty3.userState.saveUserState
import com.example.empty3.data.getAllProducts
import com.example.empty3.navigation.Screen
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.selectors.hover
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement



external fun encodeURIComponent(str: String): String

@InitSilk
fun initSilk(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        cssLayers.add("hoverStyles")
        layer("hoverStyles") {
            registerStyle(".hover-style") {
                base {
                    Modifier.color(Colors.Black)
                }
                hover {
                    Modifier.color(Color("#007BFF"))
                }
            }
        }
    }
}


@Composable
fun SearchBox(handleSearch: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .padding(8.px)
            .backgroundColor(if (isFocused) Color("#FFFFFF") else Color("#F5F5F5"))
            .border(2.px, if (isFocused) LineStyle.Solid else LineStyle.None, if (isFocused) Color("#000000") else Color("#DDDDDD"))
            .borderRadius(10.px)
            .height(40.px) // Yüksekliği azalttık
            .width(600.px) // Arama alanını genişlettik
            .styleModifier {
                property("cursor", "text")
            }
            .onClick {
                val inputElement = document.getElementById("searchInput") as? HTMLInputElement
                inputElement?.focus()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Input(
                type = InputType.Text,
                attrs = {
                    id("searchInput")
                    attr("autocomplete", "off") // Otomatik tamamlamayı kapattık
                    style {
                        width(100.percent) // Genişliği belirledik
                        height(100.percent)
                        color(Colors.Black)
                        backgroundColor(Colors.Transparent)
                        padding(0.px, 12.px) // Yatayda padding
                        paddingBottom(12.px) // Dikeyde padding ile ortalama
                        fontSize(15.px)
                        borderRadius(10.px, 0.px, 0.px, 10.px)
                        border(0.px, LineStyle.None, Colors.Transparent)
                        outline("none")
                    }
                    attr("placeholder", "Aradığınız ürün, kategori veya markayı yazınız")
                    onInput {
                        searchText = it.value
                    }
                    onFocusIn {
                        isFocused = true
                    }
                    onFocusOut {
                        isFocused = false
                    }
                    onKeyDown {
                        if (it.key == "Enter" && searchText.isNotBlank()) {
                            handleSearch(searchText)
                        }
                    }
                }
            )
            Button(
                attrs = {
                    style {
                        width(50.px)
                        height(40.px) // Yüksekliği azalttık
                        backgroundColor(Colors.Transparent)
                        border(0.px, LineStyle.None, Colors.Transparent)
                        borderRadius(0.px, 10.px, 10.px, 0.px)
                        cursor("pointer")
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        justifyContent(JustifyContent.Center)
                        paddingBottom(12.px) // Simgeyi hizalamak için padding ekledik
                    }
                    onClick {
                        if (searchText.isNotBlank()) {
                            handleSearch(searchText)
                        }
                    }
                }
            ) {
                SpanText(
                    text = "🔍",
                    modifier = Modifier.styleModifier {
                        property("color", "#FF6600")
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderLayout(
    context: PageContext,
    userState: UserState,
    content: @Composable () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var searchResult by remember { mutableStateOf<List<Product>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()



    fun handleSearch(searchText: String) {
        coroutineScope.launch {
            getAllProducts(
                onSuccess = { apiResponse ->
                    if (apiResponse is ApiListResponse.SuccessProducts) {
                        val allProducts = apiResponse.data
                        val matchedProducts = allProducts.filter {
                            it.name.equals(searchText, ignoreCase = true) ||
                                    it.category.equals(searchText, ignoreCase = true)
                        }
                        if (matchedProducts.isNotEmpty()) {
                            val firstMatch = matchedProducts.first()
                            if (firstMatch.name.equals(searchText, ignoreCase = true)) {
                                context.router.navigateTo(Screen.ProductDetailPage(firstMatch.id).route)
                            } else if (firstMatch.category.equals(searchText, ignoreCase = true)) {
                                context.router.navigateTo(Screen.CategoryPage(encodeURIComponent(firstMatch.category)).route)
                            }
                        } else {
                            searchResult = findSimilarProducts(searchText, allProducts)
                            if (searchResult.isEmpty()) {
                                searchResult = allProducts.filter {
                                    it.name.contains(searchText, ignoreCase = true) ||
                                            it.category.contains(searchText, ignoreCase = true) ||
                                            it.description?.contains(searchText, ignoreCase = true) == true
                                }
                            }
                            context.router.navigateTo(Screen.SearchResultsPage(searchText).route)
                        }
                    }
                },
                onError = {
                    println(it)
                }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .backgroundColor(Colors.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .backgroundColor(Colors.White)
        ) {
            // Boşluk ve logo + arama çubuğu satırı
            Box(modifier = Modifier.height(8.px))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(topBottom = 8.px, leftRight = 128.px)
                    .styleModifier {
                        property("position", "sticky")
                        property("top", "0")
                        property("z-index", "1000")
                        property("background-color", Colors.White.value)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpanText(
                    text = "tadıYorum",
                    modifier = Modifier
                        .color(Colors.Black)
                        .fontSize(32.px) // Font boyutunu azalttık
                        .fontFamily("Montserrat")
                        .fontWeight(FontWeight.Bold)
                        .margin(right = 16.px) // Arama butonuna yaklaştırma
                        .cursor(Cursor.Pointer) // Tıklanabilir olduğunu göstermek için
                        .onClick {
                            context.router.navigateTo(Screen.HomePage.route)
                        }
                )

                SearchBox(::handleSearch)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (userState.username.isNullOrEmpty()) {
                        SpanText(
                            text = "Giriş Yap",
                            modifier = Modifier
                                .classNames("hover-style")
                                .cursor(Cursor.Pointer)
                                .margin(leftRight = 8.px) // Arama butonuna yaklaştırma
                                .fontSize(18.px) // Font boyutunu azalttık
                                .padding(topBottom = 8.px, leftRight = 8.px)
                                .onClick {
                                    context.router.navigateTo(Screen.LoginPage.route)
                                }
                        )

                        SpanText(
                            text = "Kayıt Ol",
                            modifier = Modifier
                                .classNames("hover-style")
                                .cursor(Cursor.Pointer)
                                .margin(leftRight = 8.px) // Arama butonuna yaklaştırma
                                .fontSize(18.px) // Font boyutunu azalttık
                                .padding(topBottom = 8.px, leftRight = 8.px)
                                .onClick {
                                    context.router.navigateTo(Screen.RegisterPage.route)
                                }
                        )
                    } else {
                        Box(
                            modifier = Modifier.position(Position.Relative)
                        ) {
                            SpanText(
                                text = userState.username!!,
                                modifier = Modifier
                                    .classNames("hover-style")
                                    .cursor(Cursor.Pointer)
                                    .margin(leftRight = 8.px) // Arama butonuna yaklaştırma
                                    .fontSize(18.px) // Font boyutunu azalttık
                                    .padding(topBottom = 8.px, leftRight = 8.px)
                                    .onClick {
                                        showMenu = !showMenu
                                    }
                            )
                            if (showMenu) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .position(Position.Fixed)
                                        .onClick {
                                            showMenu = false
                                        }
                                ) {}
                                Box(
                                    modifier = Modifier
                                        .backgroundColor(Colors.White)
                                        .border(1.px, LineStyle.Solid, Color("#DDDDDD"))
                                        .borderRadius(8.px)
                                        .width(150.px)
                                        .position(Position.Absolute)
                                        .top(40.px)
                                        .right(0.px)
                                        .onClick { it.stopPropagation() }
                                ) {
                                    Column {
                                        SpanText(
                                            text = "Değerlendirmelerim",
                                            modifier = Modifier
                                                .classNames("hover-style")
                                                .cursor(Cursor.Pointer)
                                                .padding(topBottom = 8.px, leftRight = 8.px)
                                                .onClick {
                                                    context.router.navigateTo(Screen.UserReviewsPage.route)
                                                    showMenu = false
                                                }
                                        )
                                        SpanText(
                                            text = "Çıkış Yap",
                                            modifier = Modifier
                                                .classNames("hover-style")
                                                .cursor(Cursor.Pointer)
                                                .padding(topBottom = 8.px, leftRight = 8.px)
                                                .onClick {
                                                    userState.username = null
                                                    saveUserState(userState)
                                                    showMenu = false
                                                    context.router.navigateTo(Screen.HomePage.route)
                                                    window.location.reload()  // Sayfayı yenilemek için eklendi
                                                }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Kategori satırı
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .backgroundColor(Colors.White)
                    .padding(topBottom = 8.px, leftRight = 32.px),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SpanText(
                    text = "Kadın",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Kadın")).route)
                        }
                )
                SpanText(
                    text = "Erkek",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Erkek")).route)
                        }
                )
                SpanText(
                    text = "Anne & Çocuk",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Anne & Çocuk")).route)
                        }
                )
                SpanText(
                    text = "Ev & Yaşam",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Ev & Yaşam")).route)
                        }
                )
                SpanText(
                    text = "Süpermarket",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Süpermarket")).route)
                        }
                )
                SpanText(
                    text = "Kozmetik",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Kozmetik")).route)
                        }
                )
                SpanText(
                    text = "Ayakkabı & Çanta",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Ayakkabı & Çanta")).route)
                        }
                )
                SpanText(
                    text = "Elektronik",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Elektronik")).route)
                        }
                )
                SpanText(
                    text = "Spor & Outdoor",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Spor & Outdoor")).route)
                        }
                )
                SpanText(
                    text = "Çok Satanlar",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Çok Satanlar")).route)
                        }

                )
                SpanText(
                    text = "Flaş Ürünler",
                    modifier = Modifier
                        .classNames("hover-style")
                        .cursor(Cursor.Pointer)
                        .margin(leftRight = 8.px) // Marginleri azalttık
                        .fontSize(16.px) // Font boyutunu azalttık
                        .onClick {
                            context.router.navigateTo(Screen.CategoryPage(encodeURIComponent("Flaş Ürünler")).route)
                        }
                )
            }
        }

        // İçerik
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(800.px)
                .margin(top = 120.px),  // Üstteki sabit alanın yüksekliği kadar margin ekledik
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

























