package com.example.projectdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherActivity3 : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val apiKey = "aa39f5e6c6fccdfd7fe6cc2c152bd43d"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchWeatherData()
    }
    private fun fetchWeatherData() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                getWeatherInfo(latitude, longitude)
                getWeatherInfo1(latitude, longitude)
                getWeatherInfo2(latitude,longitude)
            }
        }
    }

    private fun getWeatherInfo(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val call = weatherService.getCurrentWeather(lat, lon, apiKey, "metric")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()

                    if (weatherResponse != null) {
                        val temperature = weatherResponse.main.temp
                        val location = weatherResponse.name
                        val tempMin = weatherResponse.main.temp_min
                        val tempMax = weatherResponse.main.temp_max
                        val feelsLike = weatherResponse.main.feels_like
                        val icon = weatherResponse.weather[0].icon

                        // Format ngày giờ
                        val currentDate = SimpleDateFormat("EEE, dd MMM yyyy HH:mm", Locale.getDefault())
                            .format(Date())

                        // Hiển thị thông tin trong TextView
                        findViewById<TextView>(R.id.textViewTemperature).text = "${temperature}°C"
                        findViewById<TextView>(R.id.textViewLocation).text = location
                        findViewById<TextView>(R.id.textViewDetails).text =
                            "${tempMax}° / ${tempMin}° Cảm giác như ${feelsLike}°"
                        findViewById<TextView>(R.id.textViewDate).text = currentDate


                        val imageUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                        val imageViewWeatherIcon = findViewById<ImageView>(R.id.imageViewWeatherIcon)
                        Glide.with(this@WeatherActivity3)
                            .load(imageUrl)
                            .into(imageViewWeatherIcon)


                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    private fun getWeatherInfo1(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService1::class.java)
        val call = weatherService.getHourlyWeather(lat, lon, apiKey)

        call.enqueue(object : Callback<WeatherResponse1> {
            override fun onResponse(
                call: Call<WeatherResponse1>,
                response: Response<WeatherResponse1>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {

                        val currentTime = System.currentTimeMillis() / 1000
                        val endTime = currentTime + 24 * 3600


                        val filteredWeather = weatherResponse.list.filter { weatherData ->
                            weatherData.dt in currentTime..endTime
                        }


                        setupRecyclerView(filteredWeather)
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse1>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupRecyclerView(weatherList: List<WeatherData1>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHourlyForecast)
        val adapter = HourlyForecastAdapter1(weatherList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }


    private fun getWeatherInfo2(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService1::class.java)
        val call = weatherService.getHourlyWeather(lat, lon, apiKey)

        call.enqueue(object : Callback<WeatherResponse1> {
            override fun onResponse(
                call: Call<WeatherResponse1>,
                response: Response<WeatherResponse1>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {

                        val currentTime = System.currentTimeMillis() / 1000
                        val endTime = currentTime + 24 * 3600 * 10

                        val morningWeather = mutableListOf<WeatherData1>()
                        val nightWeather = mutableListOf<WeatherData1>()

                        weatherResponse.list.forEach { weatherData ->
                            val timestamp = weatherData.dt

                            // Lấy giờ từ timestamp
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = timestamp * 1000
                            }
                            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)


                            if (hourOfDay in 11..13 && timestamp in currentTime..endTime) {
                                morningWeather.add(weatherData)
                            } else if ((hourOfDay == 4 || hourOfDay == 2 || hourOfDay == 3) && timestamp in currentTime..endTime) {
                                nightWeather.add(weatherData)
                            }
                        }


                        setupRecyclerView2(morningWeather, nightWeather)
                    }
                }

            }

            override fun onFailure(call: Call<WeatherResponse1>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    private fun setupRecyclerView2(weatherList1: List<WeatherData1>,weatherList2: List<WeatherData1>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDailyForecast)
        val adapter = WeatherAdapter(weatherList1,weatherList2)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
}