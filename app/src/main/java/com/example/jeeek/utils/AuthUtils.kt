package com.example.jeeek.utils

import android.text.TextUtils

fun validateForm(email: String, password: String): Boolean {
    if (email.isNullOrEmpty()) return false
    if (password.isNullOrEmpty()) return false
    if (password.length < 6) return false
    return true
}
