package com.github.libretube.util

import android.app.Activity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.showKeyboard() = WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
fun Activity.hideKeyboard() = WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
