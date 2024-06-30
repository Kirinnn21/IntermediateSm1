package com.dutaram.intermediatesm1.data.pref

data class UserModel(
    val name: String,
    val email: String,
    val password: String,
    val isLogin: Boolean,
    val token: String
)