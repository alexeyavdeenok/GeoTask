package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.model.Location
import com.geotask.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationListViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    val locations: StateFlow<List<Location>> = locationRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteLocation(location: Location) {
        viewModelScope.launch {
            locationRepository.deleteById(location.id)
        }
    }
}