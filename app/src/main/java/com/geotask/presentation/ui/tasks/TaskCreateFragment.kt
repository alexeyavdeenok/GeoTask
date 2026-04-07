package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.presentation.viewmodel.CreateTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskCreateFragment : Fragment(R.layout.add_task) {

    private val viewModel: CreateTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDesc = view.findViewById<EditText>(R.id.etDesc)


        view.findViewById<View>(R.id.btnTaskCancel)?.setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<View>(R.id.btnTaskSave)?.setOnClickListener {

            val title = etTitle.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (title.isBlank()) return@setOnClickListener

            viewModel.createTask(title, description = desc)

            findNavController().popBackStack()
        }
    }
}