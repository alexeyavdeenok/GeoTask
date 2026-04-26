package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geotask.R
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

        // === ПОГОДА + ГЕОЗОНА ===
        viewModel.loadWeatherAndCheckLocation()

        val tvTemp = view.findViewById<TextView>(R.id.tvWeatherTemp)
        val ivIcon = view.findViewById<ImageView>(R.id.ivWeatherIcon)
        val tvDesc = view.findViewById<TextView>(R.id.tvWeatherDesc)
        val tvCity = view.findViewById<TextView>(R.id.tvWeatherCity)

        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            if (weather != null && tvTemp != null && ivIcon != null) {
                val sign = if (weather.temperature > 0) "+" else ""
                tvTemp.text = "$sign${weather.temperature}°"
                ivIcon.setImageResource(weather.iconRes)
                tvDesc?.text = weather.description
                tvCity?.text = weather.cityName
            }
        }

        // === АКТИВНАЯ ЛОКАЦИЯ / ГЕОЗОНА ===
        viewModel.activeLocation.observe(viewLifecycleOwner) { location ->
            if (location != null) {
                // Пользователь ВНУТРИ геозоны
                btnWeatherLocation?.text = location.name
                btnWeatherLocation?.setIconResource(R.drawable.ic_location) // или другая иконка

                // Показываем контейнер с GPS-задачами и кнопку добавления
                containerWithGps?.isVisible = true
                btnAddTaskLocation?.isVisible = true

                // TODO: здесь потом фильтровать задачи по location.id
            } else {
                // Пользователь ВНЕ геозон
                btnWeatherLocation?.text = viewModel.weather.value?.cityName ?: "Неизвестно"
                btnWeatherLocation?.setIconResource(R.drawable.ic_location_off) // или ic_location

                // Скрываем контейнер с GPS-задачами и кнопку добавления с локацией
                containerWithGps?.isVisible = false
                btnAddTaskLocation?.isVisible = false
            }
        }

        // === Кнопки добавления задач ===
        btnAddTaskLocation?.setOnClickListener {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragment()
            )
        }

        view.findViewById<View>(R.id.btnAddTaskNoLocation).setOnClickListener {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragment()
            )
        }

        // === Отображение задач без геопозиции ===
        viewModel.tasks.observe(viewLifecycleOwner) { allTasks ->
            val tasksNoGps = allTasks.filter { it.locationId == null }
            val addButton = view.findViewById<View>(R.id.btnAddTaskNoLocation)

            containerNoGps.removeAllViews()

            tasksNoGps.forEach { task ->
                val taskButton = layoutInflater.inflate(
                    R.layout.item_task_button,
                    containerNoGps,
                    false
                ) as MaterialButton

                taskButton.text = task.title
                taskButton.setOnClickListener {
                    val action = TaskListFragmentDirections
                        .actionTaskListFragmentToTaskDetailFragment(task.id)
                    findNavController().navigate(action)
                }

                containerNoGps.addView(taskButton)
            }

            if (addButton.parent == null) {
                containerNoGps.addView(addButton)
            }
        }
    }
}