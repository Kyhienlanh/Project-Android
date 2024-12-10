package com.example.projectdemo

data class Post(
    val postID: String = "",
    val userID: String = "", // ID của người đăng
    val content: String = "", // Nội dung bài đăng
    val CamSucHoatDong: String? = null,
    val GanTheNguoiKhac: String? = null,
    val MauNen: String? = null,
    val imageURL: String? = null, // URL hình ảnh (nếu có)
    val timestamp: Long = System.currentTimeMillis(), // Thời gian đăng
    val likes: Int = 0, // Số lượt thích
    val commentsCount: Int = 0, // Số bình luận
    val likedBy: List<String> = listOf(), // Danh sách ID người thích bài
    val sharedCount: Int = 0, // Số lần chia sẻ
    val status: String = "public", // Trạng thái bài đăng
    val location: String? = null // Vị trí
)
