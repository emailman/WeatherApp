package edu.emailman.weatherapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import edu.emailman.weatherapp.pages.ConnectivityState
import edu.emailman.weatherapp.pages.WeatherHomeScreen
import edu.emailman.weatherapp.pages.WeatherHomeUiState
import edu.emailman.weatherapp.pages.WeatherHomeViewModel
import edu.emailman.weatherapp.ui.theme.WeatherAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client: FusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(this)

        enableEdgeToEdge()
        setContent {
                WeatherApp(client)
        }
    }
}

@Composable
fun WeatherApp(client: FusedLocationProviderClient) {

    val weatherHomeViewModel: WeatherHomeViewModel = viewModel()
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        permissionGranted = granted
    }

    LaunchedEffect(Unit) {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            launcher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            permissionGranted = true
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            client.lastLocation.addOnSuccessListener {
                weatherHomeViewModel.setLocation(it.latitude, it.longitude)
                weatherHomeViewModel.getWeatherData()
            }
        }
    }

    val connectivityState by weatherHomeViewModel.connectivityState.collectAsState()

    WeatherAppTheme {
        WeatherHomeScreen(
            onRefresh = {
                weatherHomeViewModel.uiState = WeatherHomeUiState.Loading
                weatherHomeViewModel.getWeatherData()
            },
            isConnected = connectivityState == ConnectivityState.Available,
            uiState = weatherHomeViewModel.uiState)
    }
}