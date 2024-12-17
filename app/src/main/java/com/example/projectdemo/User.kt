package com.example.projectdemo

data class User(
    var userID: String = "",
    var name: String = "",
    var email: String = "",
    var birthday: String = "",
    var gender: String = "",
    var img: String = "",
    var post: Int = 0,
    var followers: Int = 0,
    var following: Int = 0,
    var biography:String="",
    var avatar:String=""

)
