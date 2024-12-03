package edu.emailman.weatherapp.pages

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.emailman.weatherapp.data.ConnectivityRepository
import edu.emailman.weatherapp.data.CurrentWeather
import edu.emailman.weatherapp.data.ForecastWeather
import edu.emailman.weatherapp.data.WeatherRepository
import edu.emailman.weatherapp.utils.WEATHER_API_KEY
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherHomeViewModel @Inject constructor(
    private val connectivityRepository: ConnectivityRepository,
    private val weatherRepository: WeatherRepository
): ViewModel() {
    var uiState: WeatherHomeUiState by mutableStateOf(WeatherHomeUiState.Loading)

    private var latitude = 0.0
    private var longitude = 0.0

    val connectivityState: StateFlow<ConnectivityState> = connectivityRepository.connectivityState

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        uiState = WeatherHomeUiState.Error
    }

    fun setLocation(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
    }

    fun getWeatherData() {
        viewModelScope.launch(exceptionHandler) {
            uiState = try {
                val currentWeather = async { getCurrentData() }.await()
                val forecastWeather = async { getForecastData() }.await()
                WeatherHomeUiState.Success(Weather(currentWeather, forecastWeather))
            } catch (e: Exception) {
                Log.e("Weather App", e.message!!)
                WeatherHomeUiState.Error
            }
        }
    }

    private suspend fun getCurrentData() : CurrentWeather {
        val endURL = "weather?lat=$latitude&lon=$longitude&appid=$WEATHER_API_KEY&units=imperial"
        println("$latitude $longitude")
        return weatherRepository.getCurrentWeather(endURL)
    }

    private suspend fun getForecastData() : ForecastWeather {
        val endURL = "forecast?lat=$latitude&lon=$longitude&appid=$WEATHER_API_KEY&units=imperial"
        return weatherRepository.getForecastWeather(endURL)
    }
}