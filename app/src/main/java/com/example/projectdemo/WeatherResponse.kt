package com.example.projectdemo

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val name: String
)

data class Main(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val feels_like: Double
)

data class Weather(
    val icon: String
)

data class WeatherResponse1(
    val list: List<WeatherData1> // Danh sách dự báo thời tiết theo giờ
)

data class WeatherData1(
    val dt: Long,           // Timestamp
    val main: Main1,        // Thông tin nhiệt độ
    val weather: List<Weather1>, // Thông tin mô tả thời tiết
    val dt_txt: String      // Ngày/giờ (dạng chuỗi)
)

data class Main1(
    val temp: Double,        // Nhiệt độ hiện tại
    val feels_like: Double,  // Cảm giác như
    val temp_min: Double,    // Nhiệt độ thấp nhất
    val temp_max: Double     // Nhiệt độ cao nhất
)

data class Weather1(
    val icon: String,        // Mã biểu tượng thời tiết
    val description: String  // Mô tả thời tiết
)
