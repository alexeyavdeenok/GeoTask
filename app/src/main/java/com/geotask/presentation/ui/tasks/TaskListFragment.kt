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

    // Кэшируем ссылки на статические кнопки добавления,
    // чтобы они не терялись при вызове removeAllViews()
    private var btnAddNoLocation: View? = null
    private var btnAddWithLocation: MaterialButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация контейнеров
        val containerNoGps = view.findViewById<LinearLayout>(R.id.containerTasksNoGps)
        val containerWithGps = view.findViewById<LinearLayout>(R.id.containerTasksWithGps)

        // Инициализация кнопок (находим их один раз)
        btnAddNoLocation = view.findViewById(R.id.btnAddTaskNoLocation)
        btnAddWithLocation = view.findViewById(R.id.btnAddTaskLocation)
        val btnWeatherLocation = view.findViewById<MaterialButton>(R.id.btnWeatherLocation)

        // Инициализация элементов погоды
        val tvTemp = view.findViewById<TextView>(R.id.tvWeatherTemp)
        val ivIcon = view.findViewById<ImageView>(R.id.ivWeatherIcon)
        val tvDesc = view.findViewById<TextView>(R.id.tvWeatherDesc)
        val tvCity = view.findViewById<TextView>(R.id.tvWeatherCity)

        // Запуск загрузки данных
        viewModel.loadWeatherAndCheckLocation()

        // --- Наблюдатели (Observers) ---

        // Обновление погоды
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            weather?.let {
                val sign = if (it.temperature > 0) "+" else ""
                tvTemp?.text = "$sign${it.temperature}°"
                ivIcon?.setImageResource(it.iconRes)
                tvDesc?.text = it.description
                tvCity?.text = it.cityName
            }
        }

        // Обновление состояния локации (геозоны)
        viewModel.activeLocation.observe(viewLifecycleOwner) { activeLocation ->
            if (activeLocation != null) {
                btnWeatherLocation?.text = activeLocation.name
                btnWeatherLocation?.setIconResource(R.drawable.ic_location)

                // Показываем блок для GPS-задач
                containerWithGps?.isVisible = true
                btnAddWithLocation?.isVisible = true
            } else {
                btnWeatherLocation?.text = viewModel.weather.value?.cityName ?: "Неизвестно"
                btnWeatherLocation?.setIconResource(R.drawable.ic_location_off)

                // Скрываем блок для GPS-задач, если мы не в зоне
                containerWithGps?.isVisible = false
                btnAddWithLocation?.isVisible = false
            }
            // Перерисовываем списки при изменении локации
            viewModel.tasks.value?.let { updateTaskLists(containerNoGps, containerWithGps, it) }
        }

        // Основной наблюдатель за списком задач
        viewModel.tasks.observe(viewLifecycleOwner) { allTasks ->
            updateTaskLists(containerNoGps, containerWithGps, allTasks)
        }

        // --- Обработка кликов ---

        btnAddNoLocation?.setOnClickListener {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragment()
            )
        }

        btnAddWithLocation?.setOnClickListener {
            val locationId = viewModel.activeLocation.value?.id ?: -1L
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskCreateFragmentWithLocation(locationId)
            )
        }
    }

    /**
     * Общий метод для обновления обоих контейнеров
     */
    private fun updateTaskLists(
        containerNoGps: LinearLayout?,
        containerWithGps: LinearLayout?,
        allTasks: List<Task>
    ) {
        val activeLocation = viewModel.activeLocation.value

        // 1. Задачи без локации
        val tasksNoGps = allTasks.filter { it.locationId == null }
        renderTaskButtons(containerNoGps, tasksNoGps, btnAddNoLocation)

        // 2. Задачи для текущей активной локации
        if (activeLocation != null) {
            val tasksWithGps = allTasks.filter { it.locationId == activeLocation.id }
            renderTaskButtons(containerWithGps, tasksWithGps, btnAddWithLocation)
        }
    }

    /**
     * Универсальный метод отрисовки кнопок внутри контейнера
     */
    private fun renderTaskButtons(container: LinearLayout?, tasks: List<Task>, addButton: View?) {
        container?.removeAllViews() ?: return

        // Добавляем кнопки самих задач
        tasks.forEach { task ->
            val taskItem = layoutInflater.inflate(R.layout.item_task_button, container, false) as MaterialButton
            taskItem.text = task.title
            taskItem.setOnClickListener {
                findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment(task.id)
                )
            }
            container.addView(taskItem)
        }

        // Возвращаем кнопку "Добавить" в конец списка
        addButton?.let { button ->
            // Важно: отсоединяем кнопку от текущего родителя перед добавлением в новый
            (button.parent as? ViewGroup)?.removeView(button)
            container.addView(button)
        }
    }
}