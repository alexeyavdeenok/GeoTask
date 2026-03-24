package com.geotask.presentation.ui.locations

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.geotask.R

class LocationCreateFragment : Fragment(R.layout.add_place) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Обработка ввода названия и выбора координат
        view.findViewById<View>(R.id.btnTaskSave)?.setOnClickListener {
            findNavController().popBackStack()
        }
        view.findViewById<View>(R.id.btnTaskCancel)?.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}