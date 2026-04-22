package com.geotask.presentation.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.domain.model.Location
import com.geotask.presentation.viewmodel.MapPlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@AndroidEntryPoint
class MapPlacesFragment : Fragment(R.layout.map_place) {

    private val viewModel: MapPlacesViewModel by viewModels()
    private lateinit var mapView: MapView
    private val markers = mutableMapOf<Marker, Location>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MapPlacesFragment", "onViewCreated called")

        // Устанавливаем правильное состояние кнопок (Карта выбрана)
        val btnLeft = view.findViewById<TextView>(R.id.btnLeft)
        val btnRight = view.findViewById<TextView>(R.id.btnRight)
        if (btnLeft != null && btnRight != null) {
            btnRight.setBackgroundResource(R.drawable.bg_segment_selected)
            btnLeft.setBackgroundResource(android.R.color.transparent)
            btnLeft.setTextColor(requireContext().getColor(android.R.color.darker_gray))
            btnRight.setTextColor(requireContext().getColor(android.R.color.black))
            Log.d("MapPlacesFragment", "Button states updated: Map selected")
        }

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(0))
        Configuration.getInstance().userAgentValue = "GeoTask/1.0"
        Configuration.getInstance().setDebugMode(true)  // Включаем дебаг для osmdroid
        Log.d("MapPlacesFragment", "Configuration loaded with debug mode")

        mapView = view.findViewById(R.id.mapview)
        if (mapView == null) {
            Log.e("MapPlacesFragment", "MapView is null!")
            return
        }
        Log.d("MapPlacesFragment", "MapView found")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        Log.d("MapPlacesFragment", "TileSource set to MAPNIK")

        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        val center = GeoPoint(55.7558, 37.6173)
        mapView.controller.setCenter(center)
        Log.d("MapPlacesFragment", "Map centered at $center with zoom 15")

        // Загрузка мест и добавление маркеров
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.locations.collectLatest { locations ->
                Log.d("MapPlacesFragment", "Loaded ${locations.size} locations")
                updateMarkers(locations)
            }
        }

        // Кнопка "Мое местоположение"
        view.findViewById<View>(R.id.fab_my_location)?.setOnClickListener {
            Log.d("MapPlacesFragment", "My location button clicked")
            // TODO: Реализовать получение текущего местоположения
        }

        // Возврат к списку при нажатии на "Список" кнопку
        btnLeft?.setOnClickListener {
            Log.d("MapPlacesFragment", "btnLeft clicked - returning to list")
            findNavController().navigate(
                R.id.action_mapPlacesFragment_to_locationListFragment
            )
        }
    }

    private fun updateMarkers(locations: List<Location>) {
        Log.d("MapPlacesFragment", "Updating markers for ${locations.size} locations")
        mapView.overlays.clear()
        markers.clear()

        locations.forEach { location ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(location.latitude, location.longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = location.name ?: "Место"
            marker.setOnMarkerClickListener { _, _ ->
                Log.d("MapPlacesFragment", "Marker clicked for location: ${location.name}")
                selectLocation(location)
                true
            }
            mapView.overlays.add(marker)
            markers[marker] = location
        }
        mapView.invalidate()
        Log.d("MapPlacesFragment", "Map invalidated, should display tiles now")
        Log.d("MapPlacesFragment", "Markers updated and map invalidated")
    }

    private fun selectLocation(location: Location) {
        // Передача выбранного места обратно
        findNavController().previousBackStackEntry?.savedStateHandle?.set("selected_location", location)
        findNavController().popBackStack()
    }

    override fun onResume() {
        super.onResume()
        Log.d("MapPlacesFragment", "onResume called")
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("MapPlacesFragment", "onPause called")
        mapView.onPause()
    }
}