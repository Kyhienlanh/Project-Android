package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class HourlyForecastAdapter1(private val forecastList1: List<WeatherData1>) :
    RecyclerView.Adapter<HourlyForecastAdapter1.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewWeatherIcon: ImageView = itemView.findViewById(R.id.imageViewWeatherIcon)
        val textViewTemp: TextView = itemView.findViewById(R.id.textViewTemp)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherData = forecastList1[position]

        holder.textViewTime.text = formatUnixTime(weatherData.dt) // Hiển thị thời gian
        holder.textViewTemp.text = "${weatherData.main.temp}°C"

        // Tải biểu tượng thời tiết
        val iconUrl = "https://openweathermap.org/img/wn/${weatherData.weather[0].icon}@2x.png"
        Glide.with(holder.itemView.context).load(iconUrl).into(holder.imageViewWeatherIcon)
    }

    private fun formatUnixTime(unixTime: Long): String {
        val date = Date(unixTime * 1000) // Chuyển từ giây sang ms
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }

    override fun getItemCount(): Int = forecastList1.size
}
