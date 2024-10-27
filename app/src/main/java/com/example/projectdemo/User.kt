package com.example.projectdemo

data class User(
    val userID: String = "",
    val name: String = "",
    val email: String = "",
    val birthday: String = "",
    val gender: String = "",
    val img: String = "",
    val post: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    var biography:String="",
    var avatar:String=""
)
