package com.example.projectdemo

data class OneCallWeatherResponse(
    val daily: List<DailyWeatherData>,
    val hourly: List<WeatherData1>
)

data class DailyWeatherData(
    val dt: Long, // Unix timestamp
    val temp: Temperature, // Nhiệt độ tối thiểu và tối đa
    val weather: List<WeatherDescription>, // Danh sách thông tin thời tiết
    val pop: Double // Xác suất mưa (từ 0.0 đến 1.0)
)

data class Temperature(
    val min: Double, // Nhiệt độ thấp nhất
    val max: Double  // Nhiệt độ cao nhất
)

data class WeatherDescription(
    val description: String, // Mô tả thời tiết
    val icon: String // Mã biểu tượng thời tiết
)
