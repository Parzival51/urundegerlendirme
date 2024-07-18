package com.example.empty3.userState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.empty3.api.UserState
import kotlinx.browser.localStorage


fun saveUserState(userState: UserState) {
    localStorage.setItem("username", userState.username ?: "")
}

fun loadUserState(): UserState {
    val username = localStorage.getItem("username") ?: ""
    return UserState(username.takeIf { it.isNotEmpty() })
}

@Composable
fun rememberUserState(): UserState {
    val userState = remember { loadUserState() }
    return userState
}