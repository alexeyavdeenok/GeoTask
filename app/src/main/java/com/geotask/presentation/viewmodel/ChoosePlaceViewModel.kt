package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.model.Location
import com.geotask.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChoosePlaceViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    fun saveLocation(name: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val location = Location(name = name, latitude = latitude, longitude = longitude)
            locationRepository.insert(location)
        }
    }
}