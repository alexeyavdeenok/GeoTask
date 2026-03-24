package com.geotask.presentation.ui.locations

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.geotask.R

class LocationListFragment : Fragment(R.layout.places) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- КНОПКА ДОБАВИТЬ ---
        view.findViewById<View>(R.id.btnAdd)?.setOnClickListener {
            findNavController().navigate(
                R.id.action_locationListFragment_to_locationCreateFragment
            )
        }

        // --- СЕГМЕНТ ---
        val btnLeft = view.findViewById<TextView>(R.id.btnLeft)   // GPS
        val btnRight = view.findViewById<TextView>(R.id.btnRight) // Manual

        val gpsContainer = view.findViewById<View>(R.id.containerTasksWithGps)
        val manualContainer = view.findViewById<View>(R.id.containerManual)

        // по умолчанию GPS выбран
        selectGps(btnLeft, btnRight, gpsContainer, manualContainer)

        btnLeft.setOnClickListener {
            selectGps(btnLeft, btnRight, gpsContainer, manualContainer)
        }

        btnRight.setOnClickListener {
            selectManual(btnLeft, btnRight, gpsContainer, manualContainer)
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

        gps.visibility = View.VISIBLE
        manual.visibility = View.GONE
    }

    private fun selectManual(
        btnLeft: TextView,
        btnRight: TextView,
        gps: View,
        manual: View
    ) {
        btnRight.setBackgroundResource(R.drawable.bg_segment_selected)
        btnLeft.setBackgroundResource(android.R.color.transparent)

        gps.visibility = View.GONE
        manual.visibility = View.VISIBLE
    }
}