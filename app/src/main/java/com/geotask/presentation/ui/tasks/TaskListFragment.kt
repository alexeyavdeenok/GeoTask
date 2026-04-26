package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.domain.model.Location
import com.geotask.domain.model.Task
import com.geotask.presentation.viewmodel.TaskListViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val viewModel: TaskListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val containerNoGps = view.findViewById<LinearLayout>(R.id.containerTasksNoGps)
        val containerWithGps = view.findViewById<LinearLayout>(R.id.containerTasksWithGps)
        val btnAddTaskLocation = view.findViewById<MaterialButton>(R.id.btnAddTaskLocation)
        val btnWeatherLocation = view.findViewById<MaterialButton>(R.id.btnWeatherLocation)

        val tvTemp = view.findViewById<TextView>(R.id.tvWeatherTemp)
        val ivIcon = view.findViewById<ImageView>(R.id.ivWeatherIcon)
        val tvDesc = view.findViewById<TextView>(R.id.tvWeatherDesc)
        val tvCity = view.findViewById<TextView>(R.id.tvWeatherCity)

        viewModel.loadWeatherAndCheckLocation()

        // Погода
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            weather?.let {
                val sign = if (it.temperature > 0) "+" else ""
                tvTemp?.text = "$sign${it.temperature}°"
                ivIcon?.setImageResource(it.iconRes)
                tvDesc?.text = it.description
                tvCity?.text = it.cityName
            }
        }

        // Главный наблюдатель за задачами
        viewModel.tasks.observe(viewLifecycleOwner) { allTasks ->
            val activeLocation = viewModel.activeLocation.value

            updateNoLocationContainer(containerNoGps, allTasks)

            if (activeLocation != null) {
                updateWithLocationContainer(containerWithGps, btnAddTaskLocation, allTasks, activeLocation)
                containerWithGps?.isVisible = true
                btnAddTaskLocation?.isVisible = true
            } else {
                containerWithGps?.isVisible = false
                btnAddTaskLocation?.isVisible = false
            }
        }

        // Активная локация (заголовок)
        viewModel.activeLocation.observe(viewLifecycleOwner) { activeLocation ->
            if (activeLocation != null) {
                btnWeatherLocation?.text = activeLocation.name
                btnWeatherLocation?.setIconResource(R.drawable.ic_location)
            } else {
                btnWeatherLocation?.text = viewModel.weather.value?.cityName ?: "Неизвестно"
                btnWeatherLocation?.setIconResource(R.drawable.ic_location_off)
            }
        }

        // Кнопки добавления
        btnAddTaskLocation?.setOnClickListener {
            val locationId = viewModel.activeLocation.value?.id ?: -1L
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragmentWithLocation(locationId)
            )
        }

        view.findViewById<View>(R.id.btnAddTaskNoLocation)?.setOnClickListener {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragment()
            )
        }
    }

    private fun updateNoLocationContainer(container: LinearLayout?, allTasks: List<Task>) {
        container?.removeAllViews() ?: return

        val tasks = allTasks.filter { it.locationId == null }

        // Добавляем задачи без локации
        tasks.forEach { task ->
            val button = layoutInflater.inflate(R.layout.item_task_button, container, false) as MaterialButton
            button.text = task.title
            button.setOnClickListener {
                findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment(task.id)
                )
            }
            container.addView(button)
        }

        // === ВАЖНО: Добавляем кнопку "Добавить без локации" в конец ===
        val addButton = view?.findViewById<View>(R.id.btnAddTaskNoLocation)
        if (addButton != null) {
            // Убираем кнопку из предыдущего родителя, если она где-то висит
            (addButton.parent as? ViewGroup)?.removeView(addButton)
            container.addView(addButton)
        }
    }
    private fun updateWithLocationContainer(
        container: LinearLayout?,
        addButton: MaterialButton?,
        allTasks: List<Task>,
        activeLocation: Location
    ) {
        container?.removeAllViews() ?: return

        val tasks = allTasks.filter { it.locationId == activeLocation.id }

        tasks.forEach { task ->
            val button = layoutInflater.inflate(R.layout.item_task_button, container, false) as MaterialButton
            button.text = task.title
            button.setOnClickListener {
                findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment(task.id)
                )
            }
            container.addView(button)
        }

        // Добавляем кнопку "Добавить задачу с локацией" в конец
        if (addButton != null) {
            (addButton.parent as? ViewGroup)?.removeView(addButton)
            container.addView(addButton)
        }
    }
}