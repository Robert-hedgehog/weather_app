package com.example.weather_app

import java.util.Locale

data class WeatherResponse(
    val name: String = "",
    val main: Main = Main(),
    val weather: List<WeatherDescription> = emptyList(),
    val wind: Wind = Wind(),
    val clouds: Clouds = Clouds()
) {
    fun getFormattedTemp(isFahrenheit: Boolean): String {
        return if (isFahrenheit) {
            val tempF = main.temp * 9.0 / 5.0 + 32.0
            String.format(Locale.getDefault(), "%.1f °F", tempF)
        } else {
            String.format(Locale.getDefault(), "%.1f °C", main.temp)
        }
    }

    fun getWindDirectionText(): String {
        val directions = arrayOf("С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ")
        val index = ((wind.deg + 22.5) / 45.0).toInt() % 8
        return "Ветер: ${directions[index]} (${wind.deg}°), ${wind.speed} м/с"
    }

    fun getWindRotation(): Float = wind.deg.toFloat()

    fun getCloudsText(): String {
        val desc = weather.firstOrNull()?.description ?: ""
        return "$desc (облачность: ${clouds.all}%)"
    }
}

data class Main(
    val temp: Double = 0.0,
    val humidity: Int = 0
)

data class WeatherDescription(
    val main: String = "",
    val description: String = "",
    val icon: String = ""
)

data class Wind(
    val speed: Double = 0.0,
    val deg: Int = 0
)

data class Clouds(
    val all: Int = 0
)