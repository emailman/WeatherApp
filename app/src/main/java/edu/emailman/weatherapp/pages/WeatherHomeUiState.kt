package edu.emailman.weatherapp.pages

import edu.emailman.weatherapp.data.CurrentWeather
import edu.emailman.weatherapp.data.ForecastWeather

data class Weather(
    val currentWeather: CurrentWeather,
    val forecastWeather: ForecastWeather
)

sealed interface WeatherHomeUiState {
    data class Success(val weather: Weather) : WeatherHomeUiState
    data object Error : WeatherHomeUiState
    data object Loading : WeatherHomeUiState
}

sealed interface ConnectivityState {
    data object Available: ConnectivityState
    data object Unavailable: ConnectivityState
}

