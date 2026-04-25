package com.geotask.presentation.ui.locations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.domain.model.Location
import com.geotask.presentation.viewmodel.LocationListViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LocationListFragment : Fragment(R.layout.places) {

    private val viewModel: LocationListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LocationListFragment", "onViewCreated called")

        val btnAdd = view.findViewById<View>(R.id.btnAdd)
        val btnLeft = view.findViewById<TextView>(R.id.btnLeft)
        val btnRight = view.findViewById<TextView>(R.id.btnRight)
        val gpsContainer = view.findViewById<LinearLayout>(R.id.containerTasksWithGps)
        val manualContainer = view.findViewById<View>(R.id.containerManual)

        // По умолчанию показываем список
        selectGps(btnLeft, btnRight, gpsContainer, manualContainer)

        // === СПИСОК ЛОКАЦИЙ ===
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locations.collect { locations ->
                    gpsContainer.removeAllViews()

                    if (locations.isEmpty()) {
                        Log.d("LocationListFragment", "No locations yet")
                    }

                    locations.forEach { location ->
                        val button = layoutInflater.inflate(
                            R.layout.place_button, // или item_task_button
                            gpsContainer,
                            false
                        ) as MaterialButton

                        button.text = location.name ?: "Без названия"

                        button.setOnClickListener {
                            // Переход на карту (можно расширить до деталей позже)
                            findNavController().navigate(
                                R.id.action_locationListFragment_to_mapPlacesFragment
                            )
                        }

                        gpsContainer.addView(button)
                    }
                }
            }
        }

        // === КНОПКА ДОБАВИТЬ ===
        btnAdd?.setOnClickListener {
            Log.d("LocationListFragment", "btnAdd clicked")
            findNavController().navigate(
                R.id.action_locationListFragment_to_locationCreateFragment
            )
        }

        // === СЕГМЕНТЫ ===
        btnLeft?.setOnClickListener {
            selectGps(btnLeft, btnRight, gpsContainer, manualContainer)
        }

        btnRight?.setOnClickListener {
            btnRight?.setBackgroundResource(R.drawable.bg_segment_selected)
            btnLeft?.setBackgroundResource(android.R.color.transparent)
            btnLeft?.setTextColor(requireContext().getColor(android.R.color.darker_gray))
            btnRight?.setTextColor(requireContext().getColor(android.R.color.black))

            findNavController().navigate(
                R.id.action_locationListFragment_to_mapPlacesFragment
            )
        }
    }

    private fun selectGps(
        btnLeft: TextView?,
        btnRight: TextView?,
        gps: View?,
        manual: View?
    ) {
        btnLeft?.setBackgroundResource(R.drawable.bg_segment_selected)
        btnRight?.setBackgroundResource(android.R.color.transparent)
        btnLeft?.setTextColor(requireContext().getColor(android.R.color.black))
        btnRight?.setTextColor(requireContext().getColor(android.R.color.darker_gray))

        gps?.visibility = View.VISIBLE
        manual?.visibility = View.GONE
    }
}