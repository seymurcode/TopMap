package com.artileriya.uicomponents.map

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.artileriya.uicomponents.R
import com.artileriya.uicomponents.databinding.FragmentMapsBinding
import com.artileriya.uicomponents.model.artileriya.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round


@AndroidEntryPoint
class MapsFragment : Fragment() {

    val viewModel : MapsViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var requestQueue: RequestQueue
    val database = FirebaseDatabase.getInstance()
    val locationClient : GoogleLocationClient by lazy {
        GoogleLocationClient(requireContext())
    }

    lateinit var binding : FragmentMapsBinding

    var googleMap: GoogleMap? =null
    var currentMarker: Marker? = null
    var currentMarkerText: String? = null

    private val callback = OnMapReadyCallback { googleMapp ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        googleMap=googleMapp
        googleMap?.mapType=GoogleMap.MAP_TYPE_SATELLITE

        /*val baku = LatLng(40.4093, 49.8671)
        googleMap.addMarker(MarkerOptions().position(baku).title("Marker in baku"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(baku, 15f))*/
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        googleMap?.setOnMapClickListener(object :GoogleMap.OnMapClickListener {
            override fun onMapClick(latlng :LatLng) {

                setMarkerlocation(latlng)
                //Log.v("-->","Eni: "+latlng.latitude+"\n Uzunu: "+latlng.longitude)
            }
        })




    }


    fun setMarkerlocation(latlng :LatLng) {
        val location = LatLng(latlng.latitude,latlng.longitude)
        if (currentMarker != null) {
            currentMarker?.position = location
        } else {
            currentMarker = googleMap?.addMarker(MarkerOptions().position(location))
        }
        val (latitudeDMS, longitudeDMS) = convertToDMS(location)
        currentMarkerText="Eni: "+latitudeDMS+"\nUzunu: "+longitudeDMS + "\nHündürlüyü: "
        binding.location.setText(currentMarkerText)
        binding.cardView.isVisible=true
        binding.cardViewEditText.isVisible=false

        val zoom = googleMap?.cameraPosition?.zoom
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location,zoom!!))

        getElevation(location)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapsBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        requestPermissionFirst()

        binding.edit.setOnClickListener{
            binding.cardViewEditText.isVisible=true
            val (latitudeDMS, longitudeDMS) = convertLatLngToDMS(currentMarker!!.position)
            binding.latDegree.setText(latitudeDMS.degrees.toString())
            binding.latMinute.setText(latitudeDMS.minutes.toString())
            binding.latSecond.setText(latitudeDMS.seconds.toString())
            binding.longDegree.setText(longitudeDMS.degrees.toString())
            binding.longMinute.setText(longitudeDMS.minutes.toString())
            binding.longSecond.setText(longitudeDMS.seconds.toString())
        }

        binding.editAccept.setOnClickListener{
            val latitudeDMS = DMS(binding.latDegree.text.toString().toInt(), binding.latMinute.text.toString().toInt(), binding.latSecond.text.toString().toInt())
            val longitudeDMS = DMS(binding.longDegree.text.toString().toInt(), binding.longMinute.text.toString().toInt(), binding.longSecond.text.toString().toInt())

            val dmsLatLng = DMSLatLng(latitudeDMS, longitudeDMS)
            val latLng = convertDMSToLatLng(dmsLatLng)
            setMarkerlocation(latLng)
        }

        requestQueue = Volley.newRequestQueue(this.requireContext())

        val usersRef = database.getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Tüm kullanıcı verilerini al
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    Log.v("--> onDataChange", "-> "+user?.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.v("--> onCancelled", databaseError.toString())
            }
        })
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun requestPermission() {
        locationPermissionReceiver.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION))
    }
    private val locationPermissionReceiver = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if(it.values.any { isGranted-> isGranted }) {
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val myLocation = LatLng(location.latitude, location.longitude)
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
                }
            }
            Intent(requireContext(), LocationService::class.java).apply {
                requireActivity().startService(this)
            }
        }
    }

    private fun requestPermissionFirst(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(requireView(), "Hassas konum bilgine iznine ihtiyacım var", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver") {
                requestPermission()
            }.show()
        } else if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(requireView(), "Genel bi konum bilgine iznine ihtiyacım var", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver") {
                requestPermission()
            }.show()
        }  else {
            requestPermission()
        }
    }

    fun convertToDMS(latLng: LatLng): Pair<String, String> {
        val latitudeDMS = convertToDMS2(latLng.latitude)
        val longitudeDMS = convertToDMS2(latLng.longitude)
        return Pair(latitudeDMS, longitudeDMS)
    }

    fun convertToDMS2(coordinate: Double): String {
        val degrees = coordinate.toInt()
        val minutes = ((coordinate - degrees) * 60).toInt()
        val seconds = (((coordinate - degrees) * 60 - minutes) * 60).toInt()
        return "$degrees°$minutes'$seconds\""
    }


    override fun onResume() {
        super.onResume()

        viewModel.navigateNextPageEvent.observe(this) {
            if(it) {
                //openProductPage()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.navigateNextPageEvent.removeObservers(this)
        viewModel.navigateNextPageEvent.postValue(false)
    }

    fun convertLatLngToDMS(latLng: LatLng): Pair<DMS, DMS> {
        val latitudeDMS = convertToDMS(latLng.latitude)
        val longitudeDMS = convertToDMS(latLng.longitude)
        return Pair(latitudeDMS, longitudeDMS)
    }
    fun convertToDMS(coordinate: Double): DMS {
        val degrees = coordinate.toInt()
        val minutes = ((coordinate - degrees) * 60).toInt()
        val seconds = (((coordinate - degrees) * 60 - minutes) * 60).toInt()
        return DMS(Math.abs(degrees), Math.abs(minutes), Math.abs(seconds))
    }

    fun convertDMSToLatLng(dmsLatLng: DMSLatLng): LatLng {
        val latitude = convertDMSToDecimal(dmsLatLng.latitude)
        val longitude = convertDMSToDecimal(dmsLatLng.longitude)
        return LatLng(latitude, longitude)
    }

    fun convertDMSToDecimal(dms: DMS): Double {
        val decimalDegrees = dms.degrees + dms.minutes / 60.0 + dms.seconds / 3600.0
        return if (dms.degrees < 0) -decimalDegrees else decimalDegrees
    }

    private fun getElevation(location: LatLng) {
        val apiKey = "AIzaSyCCC8w20_p5RsV10XJK2RSnldiQi6rNA_8" // Buraya kendi API anahtarınızı ekleyin
        val url = "https://maps.googleapis.com/maps/api/elevation/json?locations=${location.latitude},${location.longitude}&key=$apiKey"
        Log.v("link-->",url)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val results = response.getJSONArray("results")
                if (results.length() > 0) {
                    val elevation = round(results.getJSONObject(0).getDouble("elevation")).toString()+"m"
                    // Yükseklik değerini kullanın
                    Log.v("---> elevation","Elevation: "+elevation)

                    currentMarkerText+=elevation
                    binding.location.setText(currentMarkerText)
                }
            },
            Response.ErrorListener { error ->
                // Hata durumunda işlem
                error.printStackTrace()
            }
        )

        // Request queue'ya ekleyin
        requestQueue.add(jsonObjectRequest)
    }
}

data class DMS(
    val degrees: Int,
    val minutes: Int,
    val seconds: Int
)
data class DMSLatLng(
    val latitude: DMS,
    val longitude: DMS
)

