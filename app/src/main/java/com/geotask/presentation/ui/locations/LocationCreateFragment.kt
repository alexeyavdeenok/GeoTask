package com.geotask.presentation.ui.locations

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.domain.model.Location
import com.geotask.presentation.viewmodel.CreateLocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationCreateFragment : Fragment(R.layout.add_place) {

    private val viewModel: CreateLocationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val tvLocationInfo = view.findViewById<TextView>(R.id.tvLocationInfo)
        val btnSelectLocation = view.findViewById<View>(R.id.btnSelectLocation)
        val btnSave = view.findViewById<View>(R.id.btnTaskSave)
        val btnCancel = view.findViewById<View>(R.id.btnTaskCancel)
        val tvTitle = view.findViewById<TextView>(R.id.tvTaskTitle)

        // Устанавливаем заголовок
        tvTitle.text = "Новая локация"

        // Кнопка отмены
        btnCancel?.setOnClickListener {
            findNavController().popBackStack()
        }

        // Кнопка выбора локации - открывает карту
        btnSelectLocation?.setOnClickListener {
            findNavController().navigate(
                R.id.action_locationCreateFragment_to_mapFragment
            )
        }

        // Слушаем выбранную локацию с карты
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Location>("selected_location")?.observe(viewLifecycleOwner) { location ->
            viewModel.selectLocation(location.latitude, location.longitude)
            tvLocationInfo.text = "Координаты: ${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)}"
            tvLocationInfo.visibility = View.VISIBLE
        }

        // Кнопка сохранения
        btnSave?.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (title.isBlank()) {
                etTitle.error = "Введите название"
                return@setOnClickListener
            }

            if (viewModel.selectedLocation.value == null) {
                Toast.makeText(requireContext(), "Выберите местоположение", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем локацию
            viewModel.saveLocation(title, description.ifBlank { null })
            findNavController().popBackStack()
        }
    }
}
