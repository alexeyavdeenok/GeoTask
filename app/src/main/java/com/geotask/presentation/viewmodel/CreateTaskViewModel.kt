package com.geotask.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geotask.domain.model.Task
import com.geotask.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _selectedLocationId = MutableStateFlow<Long?>(null)
    val selectedLocationId: StateFlow<Long?> = _selectedLocationId.asStateFlow()

    fun createTask(title: String, deadline: Long? = null, description: String? = null) {
        viewModelScope.launch {
            val task = Task.Builder(title)
                .locationId(selectedLocationId.value)
                .deadline(deadline)
                .description(description)
                .build()

            taskRepository.insertTask(task)
            // Здесь можно просто ничего не возвращать, а обработку успеха делать в Fragment
        }
    }

    fun onLocationSelected(locationId: Long?) {
        _selectedLocationId.value = locationId
    }
}