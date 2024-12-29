package edu.emailman.weatherapp.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.emailman.weatherapp.R
import edu.emailman.weatherapp.customuis.AppBackground
import edu.emailman.weatherapp.data.CurrentWeather
import edu.emailman.weatherapp.data.ForecastWeather
import edu.emailman.weatherapp.utils.DEGREE
import edu.emailman.weatherapp.utils.getFormattedDate
import edu.emailman.weatherapp.utils.getIconUrl
import java.util.Locale
import kotlin.text.Typography.degree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherHomeScreen(
    isConnected: Boolean,
    onRefresh: () -> Unit,
    uiState: WeatherHomeUiState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AppBackground(photoID = R.drawable.sky_background_2)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Weather App by Eric",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        actionIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .wrapContentSize()
            ) {
                if (!isConnected) {
                    Text("No Internet Connection",
                        style = MaterialTheme.typography.titleMedium
                    )
                } else {
                    when (uiState) {
                        is WeatherHomeUiState.Error -> ErrorSection("Failed to fetch data",
                            onRefresh = onRefresh)
                        is WeatherHomeUiState.Loading -> Text("Loading ...")
                        is WeatherHomeUiState.Success -> WeatherSection(weather = uiState.weather)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorSection(
    message: String,
    onRefresh: () -> Unit
) {
    Column {
        Text(message)
        Spacer(modifier = Modifier.height(8.dp))
        IconButton(
            onClick = onRefresh,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
fun WeatherSection(
    weather: Weather,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        CurrentWeatherSection(
            currentWeather = weather.currentWeather,
            modifier = Modifier.weight(1f)
        )
        ForecastWeatherSection(forecastItems = weather.forecastWeather.list!!)
    }
}

@Composable
fun CurrentWeatherSection(
    currentWeather: CurrentWeather,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${currentWeather.name}, ${currentWeather.sys?.country}",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            getFormattedDate(currentWeather.dt!!, pattern = "MMM dd yyyy"),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "${currentWeather.main?.temp?.toInt()}$DEGREE",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Feels like ${currentWeather.main?.feelsLike?.toInt()}$DEGREE",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getIconUrl(currentWeather.weather?.get(0)!!.icon!!))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Text(
                currentWeather.weather[0]!!.description!!,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    "Wind Speed " +
                            String.format(
                                Locale.getDefault(),
                                "%.0f",
                                currentWeather.wind?.speed?.toDouble() ?: 0) +
                            " mph",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Humidity ${currentWeather.main?.humidity}%",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Visibility " +
                            String.format(
                                Locale.getDefault(),
                                "%.1f",
                                currentWeather.visibility?.toDouble()?.div(1610) ?: 0)
                            + " mi",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Pressure " +
                            String.format(Locale.getDefault(),
                                "%.2f",
                                currentWeather.main?.pressure?.toDouble()?.div(33.86) ?: 0
                            )
                            + " inHg",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(10.dp))

            Surface(modifier = Modifier
                .width(2.dp)
                .height(120.dp)) {  }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text("Sunrise ${getFormattedDate(
                    currentWeather.sys?.sunrise!!,
                    pattern = "hh:mm a")}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Sunset ${getFormattedDate(
                    currentWeather.sys.sunset!!,
                    pattern = "hh:mm a")}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ForecastWeatherSection(
    forecastItems: List<ForecastWeather.ForecastItem?>,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(forecastItems.size) { index ->
            ForecastWeatherItem(forecastItems[index]!!)
        }
    }
}

@Composable
fun ForecastWeatherItem(
    item: ForecastWeather.ForecastItem,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column (
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Text(
                getFormattedDate(item.dt!!, pattern = "EEE"),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                getFormattedDate(item.dt, pattern = "HH:mm"),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(20.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getIconUrl(item.weather?.get(0)!!.icon!!))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(top = 4.dp, bottom = 4.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))

            Text(

                "${item.main?.temp?.toInt()}$degree",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}