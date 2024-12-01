package edu.emailman.weatherapp.pages

import android.app.Application
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.emailman.weatherapp.data.ConnectivityRepository
import edu.emailman.weatherapp.data.CurrentWeather
import edu.emailman.weatherapp.data.DefaultConnectivityRepository
import edu.emailman.weatherapp.data.ForecastWeather
import edu.emailman.weatherapp.data.WeatherClassImpl
import edu.emailman.weatherapp.data.WeatherRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherHomeViewModel(
    private val connectivityRepository: ConnectivityRepository
): ViewModel() {
    private val weatherRepository: WeatherRepository = WeatherClassImpl()
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
        val endURL = "weather?lat=$latitude&lon=$longitude&appid=f282e6810344a266faf4e7311d63359d&units=imperial"
        println("$latitude $longitude")
        return weatherRepository.getCurrentWeather(endURL)
    }

    private suspend fun getForecastData() : ForecastWeather {
        val endURL = "forecast?lat=$latitude&lon=$longitude&appid=f282e6810344a266faf4e7311d63359d&units=imperial"
        return weatherRepository.getForecastWeather(endURL)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as Application)
                val connectivityManager = application.getSystemService(ConnectivityManager::class.java)
                WeatherHomeViewModel(
                    connectivityRepository = DefaultConnectivityRepository(connectivityManager)
                )
            }
        }
    }
}