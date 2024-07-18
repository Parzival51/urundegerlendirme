package com.example.empty3.pages.registerandlogin

import androidx.compose.runtime.*
import com.example.empty3.api.User
import com.example.empty3.data.registerUser
import com.example.empty3.navigation.Screen
import com.example.empty3.userState.rememberUserState
import com.example.empty3.userState.saveUserState
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.margin
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.*
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.text.SpanText
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.placeholder
import org.jetbrains.compose.web.dom.*


@Page("/register")
@Composable
fun RegisterPage() {
    val context = rememberPageContext()
    val userState = rememberUserState()
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            Row(
                modifier = Modifier.margin(bottom = 16.px),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SpanText(
                    text = "Hesabınız var mı? ",
                    modifier = Modifier
                        .fontSize(16.px)
                        .color(Color("#212529"))
                        .fontFamily("Montserrat")
                )
                SpanText(
                    text = "Giriş yapın",
                    modifier = Modifier
                        .fontSize(16.px)
                        .color(Color("#007BFF"))
                        .fontFamily("Montserrat")
                        .cursor(Cursor.Pointer)
                        .onClick {
                            context.router.navigateTo(Screen.LoginPage.route)
                        }
                )
            }
            SpanText(
                text = "Kayıt Ol",
                modifier = Modifier
                    .fontSize(32.px)
                    .fontWeight(FontWeight.Bold)
                    .color(Color("#212529"))
                    .fontFamily("Montserrat")
                    .margin(bottom = 16.px)
            )
            Input(
                type = InputType.Email,
                attrs = Modifier
                    .width(50.percent)
                    .padding(bottom = 16.px)
                    .border(1.px, LineStyle.Solid, Color("#D3D3D3"))
                    .borderRadius(8.px)
                    .padding(16.px)
                    .fontFamily("Montserrat")
                    .fontSize(20.px)
                    .toAttrs {
                        value(email)
                        onInput { email = it.value }
                        placeholder("Email")
                        id("email")
                        name("email")
                    }
            )
            Div(attrs = { style { height(8.px) } }) // Inputlar arası mesafe
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
                        value(username)
                        onInput { username = it.value }
                        placeholder("Kullanıcı Adı")
                        id("username")
                        name("username")
                    }
            )
            Div(attrs = { style { height(8.px) } }) // Inputlar arası mesafe
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
            Div(attrs = { style { height(8.px) } }) // Inputlar arası mesafe
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
                        value(confirmPassword)
                        onInput { confirmPassword = it.value }
                        placeholder("Şifre Tekrarı")
                        id("confirmPassword")
                        name("confirmPassword")
                    }
            )
            Div(attrs = { style { height(16.px) } }) // Inputlar ve buton arası mesafe
            Button(
                attrs = Modifier
                    .classNames("button-style")
                    .toAttrs {
                        onClick {
                            if (password == confirmPassword) {
                                val user = User(email, username, password)
                                scope.launch {
                                    registerUser(user,
                                        onSuccess = {
                                            userState.username = username
                                            saveUserState(userState) // Kullanıcı durumunu kaydet
                                            context.router.navigateTo(Screen.HomePage.route)
                                        },
                                        onError = {
                                            println("Error registering user: ${it.message}")
                                        }
                                    )
                                }
                            } else {
                                println("Passwords do not match")
                            }
                        }
                    }
            ) {
                Text("Kayıt Ol")
            }
            Div(attrs = { style { height(16.px) } }) // Butonlar arası mesafe
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        alignItems(AlignItems.Center)
                        margin(16.px) // Yukarıdaki öğelerle daha az mesafe
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
                        // Google ile devam et işlevi buraya eklenecek
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
                    src = "/image/google-logo.png",
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
        }
    }
}













