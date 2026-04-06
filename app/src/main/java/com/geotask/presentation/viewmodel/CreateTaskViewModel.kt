package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.usecase.task.CreateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    private val _selectedLocationId = MutableStateFlow<Long?>(null)
    val selectedLocationId: StateFlow<Long?> = _selectedLocationId.asStateFlow()

    fun createTask(
        title: String,
        deadline: Long? = null,
        description: String? = null
    ) {
        viewModelScope.launch {
            createTaskUseCase(
                title = title,
                description = description,
                locationId = selectedLocationId.value,
                deadline = deadline
            )
        }
    }

    fun onLocationSelected(locationId: Long?) {
        _selectedLocationId.value = locationId
    }
}