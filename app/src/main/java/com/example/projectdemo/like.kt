package com.example.projectdemo

data class Like(
    val likeId: String = "",
    val postId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
