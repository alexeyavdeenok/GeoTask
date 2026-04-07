package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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

        // === Кнопки добавления задач ===
        view.findViewById<View>(R.id.btnAddTaskLocation).setOnClickListener {
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

            // Удаляем только задачи, но оставляем кнопку добавления
            // Для этого сначала удаляем все View, кроме кнопки добавления
            val addButton = view.findViewById<View>(R.id.btnAddTaskNoLocation)

            containerNoGps.removeAllViews()

            // 1. Добавляем все задачи без геопозиции
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

            // 2. Добавляем кнопку "Добавить задачу без локации" в самый конец
            if (addButton.parent == null) {
                containerNoGps.addView(addButton)
            }
        }
    }
}