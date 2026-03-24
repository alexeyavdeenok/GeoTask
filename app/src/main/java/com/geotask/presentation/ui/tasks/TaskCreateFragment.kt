package com.geotask.presentation.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.geotask.R

class TaskCreateFragment : Fragment(R.layout.add_task) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Возврат назад при нажатии на кнопку в хедере (если в разметке есть ID)
        view.findViewById<View>(R.id.btnTaskSave)?.setOnClickListener {
            findNavController().popBackStack()
        }
        view.findViewById<View>(R.id.btnTaskCancel)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}