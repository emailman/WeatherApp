package edu.emailman.weatherapp.data

import edu.emailman.weatherapp.network.WeatherApi

interface WeatherRepository {
    suspend fun getCurrentWeather(endUrl: String): CurrentWeather
    suspend fun getForecastWeather(endUrl: String): ForecastWeather
}

class WeatherClassImpl : WeatherRepository {
    override suspend fun getCurrentWeather(endUrl: String): CurrentWeather {
        return WeatherApi.retrofitService.getCurrentWeather(endUrl)
    }

    override suspend fun getForecastWeather(endUrl: String): ForecastWeather {
        return WeatherApi.retrofitService.getForecastWeather(endUrl)
    }
}