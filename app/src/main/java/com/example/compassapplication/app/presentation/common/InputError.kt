package com.example.compassapplication.app.presentation.common

enum class InputError(val msg: String?) { // Here we may use @StringRes
    INVALID_FORMAT("Invalid Format"),
    OUT_OF_RANGE("Given location out of range"),
    NONE(null)
}
