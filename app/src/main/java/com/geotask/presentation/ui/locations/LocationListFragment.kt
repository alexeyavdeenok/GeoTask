package com.geotask.presentation.ui.locations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.geotask.R

class LocationListFragment : Fragment(R.layout.places) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LocationListFragment", "onViewCreated called")

        // --- КНОПКА ДОБАВИТЬ ---
        view.findViewById<View>(R.id.btnAdd)?.setOnClickListener {
            Log.d("LocationListFragment", "btnAdd clicked")
            findNavController().navigate(
                R.id.action_locationListFragment_to_locationCreateFragment
            )
        }

        // --- СЕГМЕНТ ---
        val btnLeft = view.findViewById<TextView>(R.id.btnLeft)   // Список
        val btnRight = view.findViewById<TextView>(R.id.btnRight) // Карта

        val gpsContainer = view.findViewById<View>(R.id.containerTasksWithGps)
        val manualContainer = view.findViewById<View>(R.id.containerManual)

        // по умолчанию GPS выбран
        selectGps(btnLeft, btnRight, gpsContainer, manualContainer)

        btnLeft.setOnClickListener {
            Log.d("LocationListFragment", "btnLeft clicked - showing list")
            selectGps(btnLeft, btnRight, gpsContainer, manualContainer)
        }

        // btnRight открывает карту всех мест
        btnRight.setOnClickListener {
            Log.d("LocationListFragment", "btnRight clicked - opening map")
            // Меняем визуальное состояние кнопок
            btnRight.setBackgroundResource(R.drawable.bg_segment_selected)
            btnLeft.setBackgroundResource(android.R.color.transparent)
            btnLeft.setTextColor(requireContext().getColor(android.R.color.darker_gray))
            btnRight.setTextColor(requireContext().getColor(android.R.color.black))
            
            findNavController().navigate(
                R.id.action_locationListFragment_to_mapPlacesFragment
            )
        }
    }

    private fun selectGps(
        btnLeft: TextView,
        btnRight: TextView,
        gps: View,
        manual: View
    ) {
        btnLeft.setBackgroundResource(R.drawable.bg_segment_selected)
        btnRight.setBackgroundResource(android.R.color.transparent)
        btnLeft.setTextColor(requireContext().getColor(android.R.color.black))
        btnRight.setTextColor(requireContext().getColor(android.R.color.darker_gray))

        gps.visibility = View.VISIBLE
        manual.visibility = View.GONE
    }
}