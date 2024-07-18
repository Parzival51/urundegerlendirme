package com.example.empty3.pages.registerandlogin

import androidx.compose.runtime.*
import com.example.empty3.api.ApiListResponse
import com.example.empty3.api.UserCredentials
import com.example.empty3.data.loginUser
import com.example.empty3.navigation.Screen
import com.example.empty3.userState.rememberUserState
import com.example.empty3.userState.saveUserState
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.margin
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.selectors.hover
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLVideoElement


@InitSilk
fun initButtonStyles(ctx: InitSilkContext) {
    ctx.stylesheet.apply {
        cssLayers.add("buttonStyles")
        layer("buttonStyles") {
            registerStyle(".button-style") {
                base {
                    Modifier
                        .backgroundColor(Color("#007BFF"))
                        .color(Colors.White)
                        .padding(16.px)
                        .borderRadius(50.px) // Buton köşelerini yuvarla
                        .cursor(Cursor.Pointer)
                        .fontFamily("Montserrat")
                        .fontSize(20.px)
                        .width(50.percent)
                }
                hover {
                    Modifier.backgroundColor(rgb(21, 101, 192))
                }
            }
        }
    }
}


@Page("/login")
@Composable
fun LoginPage() {
    val context = rememberPageContext()
    val userState = rememberUserState()
    var usernameOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxSize().backgroundColor(Color("#F8F9FA"))
    ) {
        Box(
            modifier = Modifier.fillMaxHeight().width(50.percent).position(Position.Relative)
        ) {
            Video(
                src = "/video/animation.mp4",
                attrs = {
                    attr("autoplay", "true")
                    attr("loop", "true")
                    attr("muted", "true")
                    attr("playsinline", "true")
                    style {
                        position(Position.Absolute)
                        top(0.px)
                        left(0.px)
                        width(100.percent)
                        height(100.percent)
                        property("object-fit", "cover")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(50.percent)
                .padding(48.px)
                .display(DisplayStyle.Flex)
                .justifyContent(JustifyContent.Center)
                .alignItems(AlignItems.Center)
        ) {
            SpanText(
                text = "TadıYorum'a Hoşgeldiniz",
                modifier = Modifier
                    .fontSize(36.px)
                    .fontWeight(FontWeight.Bold)
                    .color(Color("#212529"))
                    .fontFamily("Montserrat")
                    .margin(bottom = 16.px)
            )
            SpanText(
                text = "Henüz hesabınız yok mu? ",
                modifier = Modifier
                    .fontSize(16.px)
                    .color(Color("#212529"))
                    .fontFamily("Montserrat")
            )
            SpanText(
                text = "Hemen kaydolun",
                modifier = Modifier
                    .fontSize(16.px)
                    .color(Color("#007BFF"))
                    .fontFamily("Montserrat")
                    .cursor(Cursor.Pointer)
                    .onClick {
                        context.router.navigateTo(Screen.RegisterPage.route)
                    }
                    .margin(bottom = 32.px)
            )
            SpanText(
                text = "Giriş Yap",
                modifier = Modifier
                    .fontSize(32.px)
                    .fontWeight(FontWeight.Bold)
                    .color(Color("#212529"))
                    .fontFamily("Montserrat")
                    .margin(bottom = 32.px)
            )
            Input(
                type = InputType.Text,
                attrs = Modifier
                    .width(50.percent)
                    .padding(bottom = 16.px)
                    .border(1.px, LineStyle.Solid, Color("#D3D3D3"))
                    .borderRadius(8.px)
                    .padding(16.px)
                    .fontFamily("Montserrat")
                    .fontSize(20.px)
                    .toAttrs {
                        value(usernameOrEmail)
                        onInput { usernameOrEmail = it.value }
                        placeholder("Kullanıcı Adı veya Email")
                        id("usernameOrEmail")
                        name("usernameOrEmail")
                    }
            )
            Div(attrs = { style { height(16.px) } })
            Input(
                type = InputType.Password,
                attrs = Modifier
                    .width(50.percent)
                    .padding(bottom = 16.px)
                    .border(1.px, LineStyle.Solid, Color("#D3D3D3"))
                    .borderRadius(8.px)
                    .padding(16.px)
                    .fontFamily("Montserrat")
                    .fontSize(20.px)
                    .toAttrs {
                        value(password)
                        onInput { password = it.value }
                        placeholder("Şifre")
                        id("password")
                        name("password")
                    }
            )
            Div(attrs = { style { height(32.px) } })
            Button(
                attrs = Modifier
                    .classNames("button-style")
                    .toAttrs {
                        onClick {
                            val credentials = UserCredentials(usernameOrEmail, password)
                            scope.launch {
                                loginUser(credentials,
                                    onSuccess = { response ->
                                        if (response is ApiListResponse.SuccessUser) {
                                            userState.username = response.data.username
                                            saveUserState(userState)
                                            context.router.navigateTo(Screen.HomePage.route)
                                        } else if (response is ApiListResponse.Error) {
                                            errorMessage = response.message
                                        }
                                    },
                                    onError = {
                                        errorMessage = "Geçersiz kullanıcı adı veya şifre"
                                        println("Error logging in: ${it.message}")
                                    }
                                )
                            }
                        }
                    }
            ) {
                Text("Giriş Yap")
            }
            Div(attrs = { style { height(16.px) } })
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        margin(32.px)
                    }
                }
            ) {
                Div(
                    attrs = {
                        style {
                            flexGrow(1)
                            height(1.px)
                            backgroundColor(Color("#D3D3D3"))
                        }
                    }
                )
                SpanText(
                    text = "OR",
                    modifier = Modifier
                        .margin(topBottom = 16.px)
                        .fontSize(14.px)
                        .color(Color("#A3A3A3"))
                )
                Div(
                    attrs = {
                        style {
                            flexGrow(1)
                            height(1.px)
                            backgroundColor(Color("#D3D3D3"))
                        }
                    }
                )
            }
            Button(
                attrs = {
                    onClick {
                        // Google ile giriş yap işlevi buraya eklenecek
                    }
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        backgroundColor(Color("#FFFFFF"))
                        color(Color("#000000"))
                        padding(16.px)
                        borderRadius(50.px) // Buton köşelerini yuvarla
                        border(1.px, LineStyle.Solid, Color("#DDDDDD"))
                        cursor("pointer")
                        fontFamily("Montserrat")
                        fontSize(20.px)
                        width(50.percent)
                        textAlign("center")
                    }
                }
            ) {
                Img(
                    src = "/image/google-logo.png", // Bu yolu doğru ayarlayın
                    attrs = {
                        style {
                            width(20.px)
                            height(20.px)
                            margin(right = 8.px)
                        }
                    }
                )
                Text("Google ile devam et")
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

@Composable
fun Video(src: String, attrs: AttrBuilderContext<HTMLVideoElement> = {}) {
    TagElement(
        tagName = "video",
        applyAttrs = attrs,
        content = {
            DomSideEffect {
                it.src = src
            }
        }
    )
}

















