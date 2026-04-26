package com.geotask.presentation.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.geotask.R
import com.geotask.domain.model.Location
import com.geotask.presentation.viewmodel.MapPlacesViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    // Маркеры сохранённых мест (отдельная коллекция, чтобы не трогать маркер пользователя)
    private val locationMarkers = mutableMapOf<Marker, Location>()
    private var userLocationMarker: Marker? = null

    private var isInitialLocationSet = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        when {
            fineGranted || coarseGranted -> {
                Log.d("MapPlacesFragment", "Permission granted")
                viewModel.loadUserLocation()
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("MapPlacesFragment", "Permission denied permanently")
                showPermissionDeniedDialog()
            }
            else -> {
                Toast.makeText(requireContext(), "Геолокация отключена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MapPlacesFragment", "onViewCreated called")

        checkAndRequestLocationPermission()

        // Сегмент-кнопки
        val btnLeft = view.findViewById<TextView>(R.id.btnLeft)
        val btnRight = view.findViewById<TextView>(R.id.btnRight)
        btnRight?.setBackgroundResource(R.drawable.bg_segment_selected)
        btnLeft?.setBackgroundResource(android.R.color.transparent)
        btnLeft?.setTextColor(requireContext().getColor(android.R.color.darker_gray))
        btnRight?.setTextColor(requireContext().getColor(android.R.color.black))

        // OSMDroid
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(0))
        Configuration.getInstance().userAgentValue = "GeoTask/1.0"

        mapView = view.findViewById(R.id.mapview) ?: run {
            Log.e("MapPlacesFragment", "MapView not found")
            return
        }

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(56.0104, 92.8526))

        // Подписка на список мест
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.locations.collect { locations ->
                    updateLocationMarkers(locations)
                }
            }
        }

        // Подписка на местоположение пользователя
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLocation.collect { geoPoint ->
                    if (geoPoint != null) {
                        val osmPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                        updateUserLocationMarker(osmPoint)

                        // Центрируем карту ТОЛЬКО при первом получении локации
                        if (!isInitialLocationSet) {
                            mapView.controller.animateTo(osmPoint)
                            mapView.controller.setZoom(16.0)
                            isInitialLocationSet = true
                        }
                    } else {
                        clearUserLocationMarker()
                    }
                }
            }
        }

        // FAB "Моё местоположение"
        view.findViewById<View>(R.id.fab_my_location)?.setOnClickListener {
            handleMyLocationClick()
        }

        // Кнопка "Список"
        btnLeft?.setOnClickListener {
            findNavController().navigate(
                R.id.action_mapPlacesFragment_to_locationListFragment
            )
        }
    }

    /**
     * Обновляет ТОЛЬКО маркеры сохранённых мест.
     * НЕ трогает маркер пользователя и другие overlays!
     */
    private fun updateLocationMarkers(locations: List<Location>) {
        // Удаляем только старые маркеры мест
        locationMarkers.keys.forEach { mapView.overlays.remove(it) }
        locationMarkers.clear()

        locations.forEach { location ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(location.latitude, location.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = location.name ?: "Место"
                setOnMarkerClickListener { _, _ ->
                    selectLocation(location)
                    true
                }
            }
            mapView.overlays.add(marker)
            locationMarkers[marker] = location
        }
        mapView.invalidate()
        Log.d("MapPlacesFragment", "Updated ${locations.size} location markers")
    }

    private fun selectLocation(location: Location) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set("selected_location", location)
        findNavController().popBackStack()
    }

    // === Маркер ПОЛЬЗОВАТЕЛЯ (синий круг, не пин) ===
    private fun updateUserLocationMarker(geoPoint: GeoPoint) {
        clearUserLocationMarker()

        userLocationMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER) // центр, а не ножка
            title = "Я здесь"
            icon = createUserLocationIcon()
        }

        mapView.overlays.add(userLocationMarker)
        mapView.invalidate()
        Log.d("MapPlacesFragment", "User marker set at $geoPoint")
    }

    private fun clearUserLocationMarker() {
        userLocationMarker?.let {
            mapView.overlays.remove(it)
            userLocationMarker = null
            mapView.invalidate()
        }
    }

    /** Программно рисует синий круг с белой обводкой */
    private fun createUserLocationIcon(): Drawable {
        val sizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
        val strokePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics).toInt()

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2196F3") }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = strokePx.toFloat()
        }

        val center = sizePx / 2f
        val radius = center - strokePx

        canvas.drawCircle(center, center, radius, fillPaint)
        canvas.drawCircle(center, center, radius, strokePaint)

        return BitmapDrawable(resources, bitmap)
    }

    // === Разрешения ===
    private fun checkAndRequestLocationPermission() {
        val fineGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        when {
            fineGranted || coarseGranted -> {
                Log.d("MapPlacesFragment", "Permissions already granted")
                viewModel.loadUserLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к геолокации")
            .setMessage("Приложению нужно знать ваше местоположение, чтобы показать его на карте.")
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
            .setTitle("Разрешение в настройках")
            .setMessage("Вы отключили диалог запроса. Включите геолокацию в настройках приложения.")
            .setPositiveButton("Открыть настройки") { _, _ ->
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = android.net.Uri.fromParts("package", requireContext().packageName, null)
                    }
                )
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    // === FAB "Моё местоположение" ===
    private fun handleMyLocationClick() {
        val userGeo = viewModel.centerOnUserLocation()

        if (userGeo != null) {
            val osmPoint = GeoPoint(userGeo.latitude, userGeo.longitude)

            // Гарантируем, что маркер есть на карте (пересоздаём если нужно)
            updateUserLocationMarker(osmPoint)

            mapView.controller.animateTo(osmPoint)
            mapView.controller.setZoom(16.0)
            Log.d("MapPlacesFragment", "Centering on user: $osmPoint")
        } else {
            val fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!fineGranted) {
                checkAndRequestLocationPermission()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Не удалось определить местоположение. Включите GPS.",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.loadUserLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("MapPlacesFragment", "onResume called")
        if (::mapView.isInitialized) {
            mapView.onResume()
            // Если вернулись из настроек — перепроверим
            val fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (fineGranted && viewModel.userLocation.value == null) {
                viewModel.loadUserLocation()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MapPlacesFragment", "onPause called")
        if (::mapView.isInitialized) mapView.onPause()
    }
}