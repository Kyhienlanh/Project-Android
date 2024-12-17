package com.example.projectdemo

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class userFollow(
    val IdFollow: String = "",
    val userID: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    var formattedDate: String = userFollow.formatDate(timestamp)
)
{
    // Hàm định dạng timestamp thành chuỗi ngày tháng năm
    companion object {
        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Định dạng ngày tháng năm
            val date = Date(timestamp)
            return sdf.format(date) // Trả về chuỗi ngày tháng năm
        }
    }
}