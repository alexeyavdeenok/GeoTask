package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChoosePlaceViewModel @Inject constructor(
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

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

    fun centerOnUserLocation(): GeoPoint? = _userLocation.value
}