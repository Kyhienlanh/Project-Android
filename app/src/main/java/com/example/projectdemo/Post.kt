package com.example.projectdemo
import java.text.SimpleDateFormat
import java.util.*

data class Post(
    var postID: String = "",
    var userID: String = "", // ID của người đăng
    var content: String = "", // Nội dung bài đăng
    var CamSucHoatDong: String? = null,
    var GanTheNguoiKhac: String? = null,
    var MauNen: String? = null,
    var imageURL: String? = null, // URL hình ảnh (nếu có)
    var timestamp: Long = System.currentTimeMillis(), // Thời gian đăng
    var formattedDate: String = formatDate(timestamp), // Ngày tháng năm dưới dạng chuỗi
    var likes: Int = 0, // Số lượt thích
    var commentsCount: Int = 0, // Số bình luận
    var likedBy: List<String> = listOf(), // Danh sách ID người thích bài
    var sharedCount: Int = 0, // Số lần chia sẻ
    var status: String = "public", // Trạng thái bài đăng
    var location: String? = null // Vị trí
) {
    // Hàm định dạng timestamp thành chuỗi ngày tháng năm
    companion object {
        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Định dạng ngày tháng năm
            val date = Date(timestamp)
            return sdf.format(date) // Trả về chuỗi ngày tháng năm
        }
    }
}
