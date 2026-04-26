package com.geotask.presentation.ui.locations

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.presentation.viewmodel.LocationDetailViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationDetailFragment : Fragment(R.layout.about_place) {

    private val viewModel: LocationDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val btnSave = view.findViewById<View>(R.id.btnTaskSave)
        val btnCancel = view.findViewById<View>(R.id.btnTaskCancel)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)

        // Подписка на данные локации
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.location.collect { location ->
                    location?.let {
                        // Заполняем поле только если оно пустое (чтобы не сбрасывать ввод пользователя)
                        if (etTitle.text.isEmpty()) {
                            etTitle.setText(it.name)
                        }
                    }
                }
            }
        }

        // Кнопка сохранения (Галочка)
        btnSave?.setOnClickListener {
            val name = etTitle.text.toString()
            viewModel.updateLocation(name) {
                findNavController().popBackStack()
            }
        }

        // Кнопка отмены (Крестик)
        btnCancel?.setOnClickListener {
            findNavController().popBackStack()
        }

        // Кнопка удаления (внизу экрана)
        btnDelete?.setOnClickListener {
            viewModel.deleteLocation {
                findNavController().popBackStack()
            }
        }
    }
}