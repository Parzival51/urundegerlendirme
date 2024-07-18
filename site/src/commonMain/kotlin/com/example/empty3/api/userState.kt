package com.example.empty3.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
data class UserState(
    var username: String? = null
)

