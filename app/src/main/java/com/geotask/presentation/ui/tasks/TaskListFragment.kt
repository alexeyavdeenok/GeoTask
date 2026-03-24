package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.geotask.R

class TaskListFragment : Fragment(R.layout.fragment_task_list) {  // ← вот здесь указываем layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        view.findViewById<View>(R.id.btnTask).setOnClickListener {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment()
            )
        }

        // В будущем клик по элементам списка и т.д.
    }
}