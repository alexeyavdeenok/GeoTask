package com.geotask.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.model.Location
import com.geotask.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val repository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Автоматическое получение ID из аргументов навигации
    private val locationId: Long = savedStateHandle.get<Long>("locationId") ?: -1L

    val location: StateFlow<Location?> = repository.getById(locationId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun deleteLocation(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteById(locationId)
            onSuccess()
        }
    }

    fun updateLocation(newName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val current = location.value ?: return@launch
            if (newName.isNotBlank()) {
                repository.update(current.copy(name = newName))
                onSuccess()
            }
        }
    }
}