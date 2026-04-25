package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.GeoPoint
import com.geotask.domain.model.Location
import com.geotask.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    private val _currentUserLocation = MutableStateFlow<GeoPoint?>(null)
    val currentUserLocation: StateFlow<GeoPoint?> = _currentUserLocation.asStateFlow()

    init {
        getCurrentUserLocation()
    }

    private fun getCurrentUserLocation() {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation()
            _currentUserLocation.value = location
        }
    }

    fun selectLocation(latitude: Double, longitude: Double) {
        _selectedLocation.value = Location(
            latitude = latitude,
            longitude = longitude
        )
    }

    fun saveLocation(name: String, description: String? = null) {
        val location = _selectedLocation.value
        if (location != null && name.isNotBlank()) {
            viewModelScope.launch {
                val fullLocation = location.copy(name = name)
                locationRepository.insert(fullLocation)
            }
        }
    }
}
