package com.example.luis_son_comp304sec002_lab03_ex1.ui.screens

// LocalActivity.kt
import android.app.Activity
import androidx.compose.runtime.compositionLocalOf

val LocalActivity = compositionLocalOf<Activity> {
    error("LocalActivity not provided")
}
