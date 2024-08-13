package com.artileriya.uicomponents.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface LocationClientInterface {
    fun getLocationUpdates(interval : Long) : Flow<Location>
}

class GoogleLocationClient (private val context : Context) : LocationClientInterface {
    val locationService: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0.locations.lastOrNull()?.let { location ->
                        launch {
                            send(location)
                        }
                    }
                }
            }
            val locationRequest = LocationRequest.Builder(interval).build()
            locationService.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

            awaitClose {
                locationService.removeLocationUpdates(locationCallback)
            }
        }
    }
}
