package com.example.empty3.pages.product

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.Product
import com.example.empty3.components.HeaderLayout
import com.example.empty3.data.addReview
import com.example.empty3.data.deleteReview
import com.example.empty3.data.getAllProducts
import com.example.empty3.navigation.ErrorPage
import com.example.empty3.navigation.Screen
import com.example.empty3.userState.rememberUserState
import com.varabyte.kobweb.compose.css.*
import com.varabyte.kobweb.compose.dom.svg.Path
import com.varabyte.kobweb.compose.dom.svg.Svg
import com.varabyte.kobweb.compose.foundation.layout.*
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.margin



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


@Page("/product/{id}")
@Composable
fun ProductDetailPage() {
    val context = rememberPageContext()
    val id = context.route.params["id"] ?: return ErrorPage("Ürün ID bulunamadı")

    var product by remember { mutableStateOf<Product?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userComment by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(0) }
    val userState = rememberUserState()
    val scope = rememberCoroutineScope()
    var expandedComments = remember { mutableStateMapOf<String, Boolean>() }

    fun loadProductDetails() {
        scope.launch {
            getAllProducts(
                onSuccess = { response ->
                    if (response is ApiListResponse.SuccessProducts) {
                        val products = response.data
                        product = products.find { it.id == id }
                        if (product == null) {
                            errorMessage = "Ürün bulunamadı"
                        }
                    } else if (response is ApiListResponse.Error) {
                        errorMessage = response.message
                    }
                },
                onError = { error ->
                    errorMessage = error.message ?: "Bir hata oluştu"
                }
            )
        }
    }

    LaunchedEffect(id) {
        loadProductDetails()
    }

    if (product != null) {
        HeaderLayout(context = context, userState = userState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundColor(Color("#F8F9FA"))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(topBottom = 32.px, leftRight = 128.px)
                        .backgroundColor(Color("#F8F9FA"))
                ) {
                    // Ürün Detayları ve Resim
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(topBottom = 32.px, leftRight = 64.px)
                            .flexDirection(FlexDirection.Row)
                    ) {
                        // Resim
                        Column(
                            modifier = Modifier
                                .width(40.percent)
                                .padding(topBottom = 32.px, leftRight = 64.px)
                                .border(1.px, LineStyle.Solid, Color("#6C757D"))
                                .borderRadius(8.px)
                                .overflow(Overflow.Hidden)
                                .position(Position.Relative)
                        ) {
                            Img(
                                src = product!!.imageUrl,
                                attrs = {
                                    style {
                                        width(100.percent)
                                        height(500.px)
                                        objectFit(ObjectFit.Cover)
                                        borderRadius(8.px)
                                    }
                                    alt("${product!!.name} image")
                                }
                            )
                        }

                        // Ürün Detayları ve Yorum Ekleme ve Yıldız Verme Alanı
                        Column(
                            modifier = Modifier
                                .width(55.percent)
                                .padding(leftRight = 64.px)
                                .align(Alignment.Top)
                        ) {
                            SpanText(
                                text = product!!.name, modifier = Modifier
                                    .fontSize(32.px)
                                    .fontWeight(FontWeight.Bold)
                                    .margin(topBottom = 8.px)
                                    .color(Color("#212529"))
                                    .fontFamily("Montserrat")
                            )
                            SpanText(
                                "Fiyat: ₺${product!!.price}", modifier = Modifier
                                    .fontSize(24.px)
                                    .fontWeight(FontWeight.Bold)
                                    .margin(topBottom = 8.px)
                                    .color(Color("#212529"))
                                    .fontFamily("Montserrat")
                            )
                            SpanText(
                                "Açıklama: ${product!!.description ?: "Açıklama mevcut değil"}",
                                modifier = Modifier
                                    .fontSize(16.px)
                                    .margin(topBottom = 16.px)
                                    .color(Color("#6C757D"))
                                    .fontFamily("Montserrat")
                            )

                            // Yorum Ekleme ve Yıldız Verme Alanı
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .borderRadius(8.px)
                                    .padding(16.px)
                                    .backgroundColor(Color.rgb(250, 250, 250))
                                    .styleModifier {
                                        property("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)")
                                    }
                                    .margin(topBottom = 16.px)
                            ) {
                                SpanText(
                                    "Yorum Ekle ve Yıldız Ver", modifier = Modifier
                                        .margin(topBottom = 8.px)
                                        .fontSize(24.px)
                                        .fontWeight(FontWeight.Bold)
                                        .color(Color("#212529"))
                                        .fontFamily("Montserrat")
                                )
                                TextArea(value = userComment, attrs = {
                                    onInput { event ->
                                        if (event.value.length <= 200) {
                                            userComment = event.value
                                        }
                                    }
                                    style {
                                        width(100.percent)
                                        height(100.px)
                                        property("padding", "12px")
                                        borderRadius(8.px)
                                        border(1.px, LineStyle.Solid, Color.rgb(204, 204, 204))
                                        margin(bottom = 8.px)
                                        fontFamily("Montserrat")
                                        fontSize(16.px)
                                        resize(Resize.None)
                                    }
                                    placeholder("Yorumunuzu buraya yazın...")
                                })
                                SpanText(
                                    "Puanlama:", modifier = Modifier
                                        .margin(bottom = 8.px, top = 16.px)
                                        .color(Color("#212529"))
                                        .fontFamily("Montserrat")
                                )
                                StarRating(rating = userRating) { newRating ->
                                    userRating = newRating
                                }
                                Div(attrs = {
                                    style {
                                        height(24.px)
                                    }
                                })
                                Button(
                                    attrs = {
                                        onClick {
                                            if (userState.username.isNullOrEmpty()) {
                                                context.router.navigateTo(Screen.LoginPage.route)
                                            } else {
                                                scope.launch {
                                                    addReview(id, userState.username!!, userRating, userComment,
                                                        onSuccess = {
                                                            window.location.reload()
                                                        },
                                                        onError = {
                                                            window.location.reload() // Hata durumunda da sayfayı yenileyebiliriz.
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        style {
                                            backgroundColor(Color("#007BFF"))
                                            color(Colors.White)
                                            property("padding", "12px 24px")
                                            borderRadius(8.px)
                                            cursor("pointer")
                                            fontFamily("Montserrat")
                                            fontSize(16.px)
                                            border(0.px)
                                            property("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                                        }
                                    }
                                ) {
                                    Text("Yorumu Gönder")
                                }

                                errorMessage?.let {
                                    SpanText(
                                        text = it,
                                        modifier = Modifier
                                            .color(Color("#FF0000"))
                                            .fontSize(16.px)
                                            .margin(top = 16.px)
                                    )
                                }
                            }
                        }
                    }

                    // Kullanıcı Yorumları
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(topBottom = 32.px, leftRight = 64.px)
                            .backgroundColor(Color.rgb(245, 245, 245))
                            .borderRadius(8.px)
                            .margin(topBottom = 16.px)
                    ) {
                        SpanText(
                            "Kullanıcı Yorumları", modifier = Modifier
                                .margin(topBottom = 16.px)
                                .fontSize(24.px)
                                .fontWeight(FontWeight.Bold)
                                .color(Color("#212529"))
                                .fontFamily("Montserrat")
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.Start)
                        ) {
                            product!!.comments.forEach { comment ->
                                val isExpanded = expandedComments[comment.comment] ?: false
                                Box(
                                    modifier = Modifier
                                        .padding(8.px)
                                        .backgroundColor(Colors.White)
                                        .borderRadius(8.px)
                                        .border(1.px, LineStyle.Solid, Color("#E0E0E0"))
                                        .padding(leftRight = 32.px, topBottom = 16.px)
                                        .margin(bottom = 16.px)
                                        .position(Position.Relative)
                                        .styleModifier {
                                            property("max-width", "700px") // Yorum alanı genişletildi
                                            property("word-wrap", "break-word")
                                        }
                                ) {
                                    if (comment.username == userState.username) {
                                        Div(
                                            attrs = {
                                                style {
                                                    position(Position.Absolute)
                                                    top(0.px)
                                                    right(0.px)
                                                    margin(8.px)
                                                    cursor(Cursor.Pointer)
                                                }
                                                onClick {
                                                    if (window.confirm("Yorumu silmek istediğinize emin misiniz?")) {
                                                        scope.launch {
                                                            deleteReview(id, comment.username,
                                                                onSuccess = {
                                                                    window.location.reload()
                                                                },
                                                                onError = {
                                                                    window.location.reload()
                                                                }
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        ) {
                                            SpanText(
                                                text = "❌",
                                                modifier = Modifier
                                                    .color(Color("#FF0000"))
                                                    .fontSize(20.px)

                                            )
                                        }
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            SpanText(
                                                text = comment.username,
                                                modifier = Modifier
                                                    .fontSize(16.px)
                                                    .fontWeight(FontWeight.Bold)
                                                    .color(Color("#007BFF"))
                                                    .fontFamily("Montserrat")
                                                    .margin(right = 8.px)
                                            )
                                            StarRating(
                                                rating = comment.rating,
                                                onRatingChange = {}
                                            )
                                        }
                                        SpanText(
                                            text = if (isExpanded) comment.comment else comment.comment.take(100),
                                            modifier = Modifier
                                                .fontSize(14.px)
                                                .color(Color("#212529"))
                                                .fontFamily("Montserrat")
                                                .margin(top = 4.px)
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
    } else if (errorMessage != null) {
        SpanText("Hata: $errorMessage", modifier = Modifier.padding(16.px).fontSize(20.px).color(Color("#DC3545")))
    } else {
        SpanText("Yükleniyor...", modifier = Modifier.padding(16.px).fontSize(20.px).color(Color("#212529")))
    }
}

@Composable
fun ErrorPage(message: String) {
    SpanText("Hata: $message", modifier = Modifier.padding(16.px).fontSize(20.px).color(Color("#DC3545")))
}



















