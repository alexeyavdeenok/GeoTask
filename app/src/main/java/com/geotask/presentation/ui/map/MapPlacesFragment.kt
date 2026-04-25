package com.geotask.presentation.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
    private var userLocationMarker: Marker? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        when {
            fineLocationGranted || coarseLocationGranted -> {
                Log.d("MapPlacesFragment", "Location permission granted")
                viewModel.loadUserLocation()
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("MapPlacesFragment", "Location permission denied - show settings dialog")
                showPermissionDeniedDialog()
            }
            else -> {
                Log.d("MapPlacesFragment", "Location permission denied - but can ask again")
                Toast.makeText(requireContext(), "Разрешение на доступ к геолокации отказано", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MapPlacesFragment", "onViewCreated called")

        // Проверка и запрос разрешений на геолокацию
        checkAndRequestLocationPermission()

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
        val center = GeoPoint(56.0104, 92.8526)
        mapView.controller.setCenter(center)
        Log.d("MapPlacesFragment", "Map centered at $center with zoom 15")

        // Загрузка мест и добавление маркеров
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.locations.collectLatest { locations ->
                Log.d("MapPlacesFragment", "Loaded ${locations.size} locations")
                updateMarkers(locations)
            }
        }

        // Подписка на местоположение пользователя
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userLocation.collectLatest { geoPoint ->
                if (geoPoint != null) {
                    Log.d("MapPlacesFragment", "User location updated: $geoPoint")
                    val osmGeoPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                    updateUserLocationMarker(osmGeoPoint)
                } else {
                    Log.d("MapPlacesFragment", "User location is null")
                    clearUserLocationMarker()
                }
            }
        }

        // Кнопка "Мое местоположение"
        view.findViewById<View>(R.id.fab_my_location)?.setOnClickListener {
            Log.d("MapPlacesFragment", "My location button clicked")
            handleMyLocationClick()
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

    private fun checkAndRequestLocationPermission() {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        val fineGranted = ContextCompat.checkSelfPermission(requireContext(), fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(requireContext(), coarseLocationPermission) == PackageManager.PERMISSION_GRANTED

        when {
            fineGranted || coarseGranted -> {
                Log.d("MapPlacesFragment", "Location permissions already granted")
                viewModel.loadUserLocation()
            }
            shouldShowRequestPermissionRationale(fineLocationPermission) -> {
                Log.d("MapPlacesFragment", "Showing permission rationale dialog")
                showPermissionRationaleDialog()
            }
            else -> {
                Log.d("MapPlacesFragment", "Requesting location permissions")
                requestPermissionLauncher.launch(arrayOf(fineLocationPermission, coarseLocationPermission))
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к геолокации")
            .setMessage("Приложению требуется доступ к вашему местоположению для отображения вас на карте.")
            .setPositiveButton("Разрешить") { _, _ ->
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Разрешение отказано")
            .setMessage("Для отображения вашего местоположения на карте, пожалуйста, разрешите доступ к геолокации в настройках приложения.")
            .setPositiveButton("Открыть настройки") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = android.net.Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun handleMyLocationClick() {
        val userGeoPoint = viewModel.centerOnUserLocation()
        when {
            userGeoPoint != null -> {
                Log.d("MapPlacesFragment", "Centering map on user location: $userGeoPoint")
                val osmGeoPoint = GeoPoint(userGeoPoint.latitude, userGeoPoint.longitude)
                mapView.controller.animateTo(osmGeoPoint)
                mapView.controller.setZoom(16.0)
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED -> {
                Log.d("MapPlacesFragment", "Permissions not granted, requesting")
                checkAndRequestLocationPermission()
            }
            else -> {
                Log.d("MapPlacesFragment", "Location not available yet")
                Toast.makeText(requireContext(), "Не удалось определить ваше местоположение", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserLocationMarker(geoPoint: GeoPoint) {
        // Удалим старый маркер если есть
        clearUserLocationMarker()

        // Создаем новый маркер пользователя
        userLocationMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = "Мое местоположение"
            // Используем синий цвет для отличия от обычных маркеров
            infoWindow = null
        }

        // Добавляем маркер на карту
        mapView.overlays.add(userLocationMarker)
        mapView.invalidate()
        Log.d("MapPlacesFragment", "User location marker added at $geoPoint")
    }

    private fun clearUserLocationMarker() {
        userLocationMarker?.let {
            mapView.overlays.remove(it)
            userLocationMarker = null
            mapView.invalidate()
            Log.d("MapPlacesFragment", "User location marker removed")
        }
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