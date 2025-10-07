package com.example.androidphpmysql

import java.io.Serializable

data class User(
    var name: String = "",
    var email: String = "",
    var role: String = "",
    var status: String = ""
) : Serializable
