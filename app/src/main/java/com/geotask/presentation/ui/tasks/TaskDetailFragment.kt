package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.geotask.R
import com.geotask.presentation.viewmodel.TaskDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailFragment : Fragment(R.layout.about_task) {

    private val viewModel: TaskDetailViewModel by viewModels()
    private val args: TaskDetailFragmentArgs by navArgs()

    private lateinit var etTitle: EditText
    private lateinit var etDesc: EditText
    private lateinit var tvTaskTitle: TextView   // заголовок в хедере

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etTitle = view.findViewById(R.id.etTitle)
        etDesc = view.findViewById(R.id.etDesc)
        tvTaskTitle = view.findViewById(R.id.tvTaskTitle)

        val taskId = args.taskId
        if (taskId == -1L) {
            findNavController().popBackStack()
            return
        }

        viewModel.loadTask(taskId)

        // Заполняем данные
        viewModel.task.observe(viewLifecycleOwner) { task ->
            task?.let {
                etTitle.setText(it.title)
                etDesc.setText(it.description ?: "")
                tvTaskTitle.text = it.title
            } ?: findNavController().popBackStack()
        }

        // Кнопка "Сохранить" в хедере
        view.findViewById<View>(R.id.btnTaskSave)?.setOnClickListener {
            val newTitle = etTitle.text.toString().trim()
            val newDesc = etDesc.text.toString().trim()

            if (newTitle.isNotBlank()) {
                viewModel.updateTask(newTitle, newDesc)
                findNavController().popBackStack()
            }
        }

        // Кнопка "Отмена" в хедере
        view.findViewById<View>(R.id.btnTaskCancel)?.setOnClickListener {
            findNavController().popBackStack()
        }

        // Кнопка "Задача выполнена" (пока как удаление, как ты просил)
        view.findViewById<View>(R.id.btnComplete)?.setOnClickListener {
            viewModel.completeTask()           // временно удаляем
            findNavController().popBackStack()
        }

        // Кнопка "Удалить"
        view.findViewById<View>(R.id.btnDelete)?.setOnClickListener {
            viewModel.deleteTask()
            findNavController().popBackStack()
        }

        // Кнопка "Выбрать место"
        view.findViewById<View>(R.id.btnAdd)?.setOnClickListener {
            // TODO: выбор места позже
        }
    }
}