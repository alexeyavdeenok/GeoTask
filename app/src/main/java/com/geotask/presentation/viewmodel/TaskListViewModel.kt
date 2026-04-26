package com.geotask.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.Location
import com.geotask.domain.model.Task
import com.geotask.domain.model.Weather
import com.geotask.domain.repository.LocationRepository
import com.geotask.domain.repository.TaskRepository
import com.geotask.domain.repository.WeatherRepository
import com.geotask.domain.util.distanceBetween
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    val tasks: LiveData<List<Task>> = taskRepository.getAllActiveTasks()

    private val _weather = MutableLiveData<Weather?>()
    val weather: LiveData<Weather?> = _weather

    // Активная локация: null = пользователь не в геозоне
    private val _activeLocation = MutableLiveData<Location?>()
    val activeLocation: LiveData<Location?> = _activeLocation

    // Радиус геозоны в метрах (потом заменим на настройки)
    private val geofenceRadius = 100.0

    fun loadWeatherAndCheckLocation() {
        viewModelScope.launch {
            // 1. Получаем позицию пользователя
            val userLocation = try {
                locationProvider.getCurrentLocation()
            } catch (e: SecurityException) {
                null
            }

            val userLat = userLocation?.latitude
            val userLon = userLocation?.longitude

            // 2. Загружаем погоду (fallback: Красноярск)
            val weatherLat = userLat ?: 56.0104
            val weatherLon = userLon ?: 92.8526

            val weatherResult = weatherRepository.getCurrentWeather(weatherLat, weatherLon)
            weatherResult.onSuccess { _weather.value = it }
            weatherResult.onFailure { _weather.value = null }

            // 3. Проверяем геозоны
            if (userLat != null && userLon != null) {
                checkGeofences(userLat, userLon)
            } else {
                _activeLocation.value = null
            }
        }
    }

    private suspend fun checkGeofences(userLat: Double, userLon: Double) {
        val locations = locationRepository.getAll().first() // берём текущий список

        val nearby = locations.firstOrNull { location ->
            val distance = distanceBetween(
                userLat, userLon,
                location.latitude, location.longitude
            )
            Log.d("TaskListViewModel", "Distance to ${location.name}: ${distance.toInt()}m")
            distance <= geofenceRadius
        }

        _activeLocation.value = nearby
        Log.d("TaskListViewModel", "Active location: ${nearby?.name ?: "none"}")
    }
}