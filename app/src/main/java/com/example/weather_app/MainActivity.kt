package com.example.weather_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.weather_app.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedCity: String = "Ангарск"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.isFahrenheit = false
        binding.showWind = true

        setupListeners()
    }

    private fun setupListeners() {
        binding.citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCity = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.scaleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.isFahrenheit = (checkedId == R.id.radioFahrenheit)
        }

        binding.windCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.showWind = isChecked
        }

        binding.searchBtn.setOnClickListener {
            loadWeather(selectedCity)
        }
    }

    private fun loadWeather(city: String) {
        lifecycleScope.launch {
            try {
                val data = WeatherApi.retrofitService.getCurrentWeather("$city,RU")
                binding.weather = data

                val iconCode = data.weather.firstOrNull()?.icon
                if (!iconCode.isNullOrEmpty()) {
                    val bitmap = loadWeatherIcon(iconCode)
                    binding.weatherIcon.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Ошибка загрузки погоды: ${e.localizedMessage ?: e.toString()}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private suspend fun loadWeatherIcon(iconCode: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://openweathermap.org/img/wn/$iconCode@2x.png")
                url.openStream().use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}