package com.example.projectdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherAdapter(private val weatherList1: List<WeatherData1>,private val weatherList2: List<WeatherData1>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewWeatherIconMorning: ImageView = itemView.findViewById(R.id.imageViewWeatherIconMorning)
        val imageViewWeatherIconNight: ImageView = itemView.findViewById(R.id.imageViewWeatherIconNight)
        val textViewTempMaxMorning: TextView = itemView.findViewById(R.id.textViewTempMaxMorning)
        val textViewTempMaxNight: TextView = itemView.findViewById(R.id.textViewTempMaxNight)
        val textViewDate:TextView=itemView.findViewById(R.id.textViewDate)
        //val textViewRainProbabilityMorning:TextView=itemView.findViewById(R.id.textViewRainProbabilityMorning)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherData1 = weatherList1[position]
        val weatherData2 = weatherList2[position]
        holder.textViewTempMaxNight.text = "${weatherData2.main.temp}°C"
        holder.textViewTempMaxMorning.text = "${weatherData1.main.temp}°C"
        val formattedDate = formatDate(weatherData1.dt_txt)
        holder.textViewDate.text = formattedDate
        //holder.textViewRainProbabilityMorning.text=weatherData1.main.feels_like.toString()
        // Tải biểu tượng thời tiết
        val iconUrl1 = "https://openweathermap.org/img/wn/${weatherData1.weather[0].icon}@2x.png"
        Glide.with(holder.itemView.context).load(iconUrl1).into(holder.imageViewWeatherIconMorning)
        val iconUrl2 = "https://openweathermap.org/img/wn/${weatherData2.weather[0].icon}@2x.png"
        Glide.with(holder.itemView.context).load(iconUrl2).into(holder.imageViewWeatherIconNight)
    }

    private fun formatUnixTime(unixTime: Long): String {
        val date = Date(unixTime * 1000) // Chuyển từ giây sang ms
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
    private fun formatDate(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateTime // Nếu xảy ra lỗi, trả về chuỗi gốc
        }
    }

    override fun getItemCount(): Int = weatherList1.size
}
