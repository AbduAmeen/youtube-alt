package com.github.libretube.util

import androidx.fragment.app.Fragment

fun Fragment.showKeyboard() {
    requireActivity().showKeyboard()
}

fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}
