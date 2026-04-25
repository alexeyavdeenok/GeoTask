package com.geotask.data.external.adapter

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.geotask.domain.location.LocationProvider
import com.geotask.domain.model.GeoPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AndroidLocationAdapter @Inject constructor(
    private val fusedClient: FusedLocationProviderClient
) : LocationProvider {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoPoint? = suspendCancellableCoroutine { cont ->
        fusedClient.lastLocation.addOnSuccessListener { location ->
            val point = location?.let { GeoPoint(it.latitude, it.longitude) }
            cont.resume(point)
        }.addOnFailureListener {
            cont.resume(null)
        }
    }
}