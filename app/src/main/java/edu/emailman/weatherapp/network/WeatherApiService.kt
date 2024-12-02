package edu.emailman.weatherapp.network

import edu.emailman.weatherapp.data.CurrentWeather
import edu.emailman.weatherapp.data.ForecastWeather
import retrofit2.http.GET
import retrofit2.http.Url

interface WeatherApiService {
    @GET
    suspend fun getCurrentWeather(@Url endUrl: String): CurrentWeather

    @GET
    suspend fun getForecastWeather(@Url endUrl: String): ForecastWeather
}
