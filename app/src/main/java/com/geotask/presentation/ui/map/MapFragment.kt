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
import com.geotask.presentation.viewmodel.ChoosePlaceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    private var selectedMarker: Marker? = null      // маркер выбранного места (пин)
    private var radiusCircle: Polygon? = null       // круг радиуса
    private var userLocationMarker: Marker? = null  // маркер "я здесь" (синий круг)

    private val radiusMeters = 500.0
    private var isInitialLocationSet = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        when {
            fineGranted || coarseGranted -> {
                Log.d("MapFragment", "Permission granted")
                viewModel.loadUserLocation()
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                Toast.makeText(requireContext(), "Геолокация отключена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Разрешения
        checkAndRequestLocationPermission()

        // 2. Карта
        Configuration.getInstance().load(requireContext(), requireActivity().getPreferences(0))
        Configuration.getInstance().userAgentValue = "GeoTask/1.0"

        mapView = view.findViewById(R.id.mapview) ?: run {
            Log.e("MapFragment", "MapView not found")
            return
        }

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.controller.setZoom(15.0)

        // Fallback — Красноярск, пока не придёт реальная локация
        mapView.controller.setCenter(GeoPoint(56.0104, 92.8526))

        // 3. Подписка на геолокацию пользователя
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLocation.collect { geoPoint ->
                    if (geoPoint != null) {
                        val osmPoint = GeoPoint(geoPoint.latitude, geoPoint.longitude)
                        updateUserLocationMarker(osmPoint)

                        // Центрируем ТОЛЬКО при первом получении
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

        // 4. Кнопка "Назад"
        view.findViewById<View>(R.id.btnBack)?.setOnClickListener {
            findNavController().popBackStack()
        }

        // 5. Тап по карте — выбор места
        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                addOrMoveMarker(p)
                return true
            }
            override fun longPressHelper(p: GeoPoint): Boolean = false
        })
        mapView.overlays.add(eventsOverlay)

        // 6. FAB "Моё местоположение"
        view.findViewById<View>(R.id.fab_my_location)?.setOnClickListener {
            handleMyLocationClick()
        }

        // 7. FAB "Сохранить"
        view.findViewById<View>(R.id.fab_save)?.setOnClickListener {
            selectedMarker?.let { marker ->
                val gp = marker.position
                val location = Location(
                    latitude = gp.latitude,
                    longitude = gp.longitude,
                    name = "Выбранное место"
                )
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_location", location)
                findNavController().popBackStack()
            } ?: Toast.makeText(requireContext(), "Выберите место на карте", Toast.LENGTH_SHORT).show()
        }
    }

    // === Маркер ВЫБРАННОГО места (стандартный пин) + радиус ===
    private fun addOrMoveMarker(geoPoint: GeoPoint) {
        selectedMarker?.let { mapView.overlays.remove(it) }
        radiusCircle?.let { mapView.overlays.remove(it) }

        selectedMarker = Marker(mapView).apply {
            position = geoPoint
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Выбранное место"
        }
        mapView.overlays.add(selectedMarker)

        radiusCircle = createRadiusCircle(geoPoint, radiusMeters)
        mapView.overlays.add(radiusCircle)

        mapView.invalidate()
    }

    private fun createRadiusCircle(center: GeoPoint, radiusMeters: Double): Polygon {
        val circle = Polygon()
        val points = mutableListOf<GeoPoint>()
        val earthRadius = 6371000.0
        val latRad = Math.toRadians(center.latitude)
        val lonRad = Math.toRadians(center.longitude)

        for (i in 0..360 step 10) {
            val angle = Math.toRadians(i.toDouble())
            val lat = Math.asin(
                Math.sin(latRad) * Math.cos(radiusMeters / earthRadius) +
                        Math.cos(latRad) * Math.sin(radiusMeters / earthRadius) * Math.cos(angle)
            )
            val lon = lonRad + Math.atan2(
                Math.sin(angle) * Math.sin(radiusMeters / earthRadius) * Math.cos(latRad),
                Math.cos(radiusMeters / earthRadius) - Math.sin(latRad) * Math.sin(lat)
            )
            points.add(GeoPoint(Math.toDegrees(lat), Math.toDegrees(lon)))
        }

        circle.points = points
        circle.fillColor = Color.argb(48, 0, 0, 255)
        circle.strokeColor = Color.BLUE
        circle.strokeWidth = 2.0f
        return circle
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
    }

    private fun clearUserLocationMarker() {
        userLocationMarker?.let {
            mapView.overlays.remove(it)
            userLocationMarker = null
            mapView.invalidate()
        }
    }

    /** Создаёт синий круг с белой обводкой программно */
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
        val fine = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)

        when {
            fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED -> {
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
            .setMessage("Чтобы показать ваше местоположение на карте, нужно разрешение.")
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
            .setMessage("Вы отключили запросы. Включите геолокацию в настройках приложения.")
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
            mapView.controller.animateTo(osmPoint)
            mapView.controller.setZoom(16.0)
        } else {
            val fineGranted = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!fineGranted) {
                checkAndRequestLocationPermission()
            } else {
                Toast.makeText(requireContext(), "Не удалось определить местоположение", Toast.LENGTH_SHORT).show()
                viewModel.loadUserLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
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
        if (::mapView.isInitialized) mapView.onPause()
    }
}