package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.presentation.viewmodel.CreateTaskViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskCreateFragment : Fragment(R.layout.add_task) {

    private val viewModel: CreateTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDesc = view.findViewById<EditText>(R.id.etDesc)
        val btnChooseLocation = view.findViewById<MaterialButton>(R.id.btnAdd)

        val args = TaskCreateFragmentArgs.fromBundle(requireArguments())
        val locationId = args.locationId

        if (locationId != -1L) {
            btnChooseLocation.text = "Выбрано: Геозона"   // потом заменим на название
            btnChooseLocation.isEnabled = false
            viewModel.onLocationSelected(locationId)
        } else {
            btnChooseLocation.text = "Выбрать местоположение"
            viewModel.onLocationSelected(null)
        }

        view.findViewById<View>(R.id.btnTaskSave)?.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (title.isBlank()) return@setOnClickListener

            viewModel.createTask(title = title, description = desc)
            findNavController().popBackStack()
        }

        view.findViewById<View>(R.id.btnTaskCancel)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}