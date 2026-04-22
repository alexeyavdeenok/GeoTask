package com.geotask.presentation.ui.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.presentation.viewmodel.ChoosePlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.choose_place) {

    private val viewModel: ChoosePlaceViewModel by viewModels()
    private lateinit var mapView: MapView
    private var selectedMarker: Marker? = null
    private var radiusCircle: Polygon? = null
    private val radiusMeters = 500.0 // Радиус в метрах

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MapFragment", "onViewCreated called")

        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(0))
        Log.d("MapFragment", "Configuration loaded")

        mapView = view.findViewById(R.id.mapview)
        if (mapView == null) {
            Log.e("MapFragment", "MapView is null!")
            return
        }
        Log.d("MapFragment", "MapView found")

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        Log.d("MapFragment", "TileSource set to MAPNIK")

        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        val center = GeoPoint(55.7558, 37.6173)
        mapView.controller.setCenter(center)
        Log.d("MapFragment", "Map centered at $center with zoom 15")

        // Клик по карте для добавления маркера
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                Log.d("MapFragment", "Map tapped at $p")
                addOrMoveMarker(p)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                Log.d("MapFragment", "Map long pressed at $p")
                return false
            }
        }
        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
        Log.d("MapFragment", "MapEventsOverlay added")

        // Кнопка "Мое местоположение"
        view.findViewById<View>(R.id.fab_my_location)?.setOnClickListener {
            Log.d("MapFragment", "My location button clicked")
            // TODO: Реализовать получение текущего местоположения
        }

        // Кнопка сохранения
        view.findViewById<View>(R.id.fab_save)?.setOnClickListener {
            Log.d("MapFragment", "Save button clicked")
            selectedMarker?.let { marker ->
                val geoPoint = marker.position
                Log.d("MapFragment", "Saving location at $geoPoint")
                viewModel.saveLocation("Новое место", geoPoint.latitude, geoPoint.longitude)
                findNavController().popBackStack()
            } ?: Log.w("MapFragment", "No marker selected to save")
        }
    }

    private fun addOrMoveMarker(geoPoint: GeoPoint) {
        Log.d("MapFragment", "Adding/moving marker to $geoPoint")
        // Удалить старый маркер и круг
        selectedMarker?.let { mapView.overlays.remove(it) }
        radiusCircle?.let { mapView.overlays.remove(it) }

        // Добавить новый маркер
        selectedMarker = Marker(mapView)
        selectedMarker?.position = geoPoint
        selectedMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        selectedMarker?.title = "Выбранное место"
        mapView.overlays.add(selectedMarker)
        Log.d("MapFragment", "Marker added at $geoPoint")

        // Добавить круг радиуса
        radiusCircle = createRadiusCircle(geoPoint, radiusMeters)
        mapView.overlays.add(radiusCircle)
        Log.d("MapFragment", "Radius circle added")

        mapView.invalidate()
        Log.d("MapFragment", "Map invalidated")
    }

    private fun createRadiusCircle(center: GeoPoint, radiusMeters: Double): Polygon {
        val circle = Polygon()
        val points = mutableListOf<GeoPoint>()

        val earthRadius = 6371000.0 // Радиус Земли в метрах
        val latRad = Math.toRadians(center.latitude)
        val lonRad = Math.toRadians(center.longitude)

        // Создать точки круга
        for (i in 0..360 step 10) {
            val angle = Math.toRadians(i.toDouble())
            val lat = Math.asin(Math.sin(latRad) * Math.cos(radiusMeters / earthRadius) +
                    Math.cos(latRad) * Math.sin(radiusMeters / earthRadius) * Math.cos(angle))
            val lon = lonRad + Math.atan2(Math.sin(angle) * Math.sin(radiusMeters / earthRadius) * Math.cos(latRad),
                    Math.cos(radiusMeters / earthRadius) - Math.sin(latRad) * Math.sin(lat))
            points.add(GeoPoint(Math.toDegrees(lat), Math.toDegrees(lon)))
        }

        circle.points = points
        circle.fillColor = Color.argb(48, 0, 0, 255) // Полупрозрачный синий
        circle.strokeColor = Color.BLUE
        circle.strokeWidth = 2.0f
        return circle
    }

    override fun onResume() {
        super.onResume()
        Log.d("MapFragment", "onResume called")
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("MapFragment", "onPause called")
        mapView.onPause()
    }
}