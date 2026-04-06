package com.geotask.presentation.ui.tasks

import com.geotask.presentation.viewmodel.TaskListViewModel
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_task_list) {  // ← вот здесь указываем layout
    private val viewModel: TaskListViewModel by viewModels()
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
        val container = view.findViewById<LinearLayout>(R.id.containerTasksNoGps)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->



            tasks.forEach { task ->

                val button = MaterialButton(requireContext()).apply {
                    text = task.title
                }

                container.addView(button)
            }
        }

        }
    }