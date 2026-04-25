package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.GeoPoint
import com.geotask.domain.model.Location
import com.geotask.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapPlacesViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    val locations = locationRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.deleteById(location.id)
        }
    }

    fun loadUserLocation() {
        viewModelScope.launch {
            try {
                val location = locationProvider.getCurrentLocation()
                _userLocation.value = location
            } catch (e: SecurityException) {
                _userLocation.value = null
            }
        }
    }

    fun centerOnUserLocation(): GeoPoint? {
        return _userLocation.value
    }
}