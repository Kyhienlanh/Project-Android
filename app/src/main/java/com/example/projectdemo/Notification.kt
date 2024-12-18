package com.example.projectdemo

data class Notification(
    val message: String? = null,
    val timestamp: Long? = null,
    val postID: String? = null,
    val type: String? = null,
    val sent: Boolean = false // Mới thêm thuộc tính sent để theo dõi trạng thái đã gửi
)