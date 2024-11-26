package com.example.weather

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var temperatureTextView: TextView
    private lateinit var getWeatherButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        temperatureTextView = findViewById(R.id.temperatureTextView)
        getWeatherButton = findViewById(R.id.getWeatherButton)

        getWeatherButton.setOnClickListener {
            getWeatherData("Kostroma", "8d83a2c2536d7a5713e3fb4bffc192bc")
        }
    }

    private fun getWeatherData(city: String, apiKey: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    temperatureTextView.text = "Ошибка: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    try {
                        val jsonObject = JSONObject(body)
                        val main = jsonObject.optJSONObject("main")
                        if (main != null) {
                            val temp = main.optDouble("temp") //
                            if (!temp.isNaN()) {
                                temperatureTextView.text = "Температура в $city: ${temp}°C"
                            } else {
                                temperatureTextView.text = "Ошибка: Не удалось получить температуру"
                            }
                        } else {
                            temperatureTextView.text = "Ошибка: Отсутствует ключ 'main'"
                        }
                    } catch (e: Exception) {
                        temperatureTextView.text = "Ошибка: ${e.message}"
                    }
                }
            }
        })
    }
}


