package com.asemlab.quakes.ui.home

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asemlab.quakes.R
import com.asemlab.quakes.base.BaseFragment
import com.asemlab.quakes.databinding.FragmentHomeBinding
import com.asemlab.quakes.datastore.SettingsDatastore
import com.asemlab.quakes.model.EarthquakesUI
import com.asemlab.quakes.ui.models.MarkerItem
import com.asemlab.quakes.utils.ColorClusterRenderer
import com.asemlab.quakes.utils.ENABLE_LOCATION_REQUEST_CODE
import com.asemlab.quakes.utils.isConnected
import com.asemlab.quakes.utils.isNightModeOn
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.toTimeString
import com.blankj.utilcode.util.LogUtils
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date


@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private var currentPosition = LatLng(31.975429, 35.860139)
    private var currentZoom = 2f
    private lateinit var clusterManager: ClusterManager<MarkerItem>
    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private var requestLocation = true
    private lateinit var datastore: SettingsDatastore
    private var earthquakeUIAdapter = EarthquakeUIAdapter(emptyList()) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToEventDetailsFragment(
                it
            )
        )
    }
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            it?.let {
                if (it)
                    checkLocationSettings()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        datastore = SettingsDatastore.getInstance(requireContext())

        val options = GoogleMapOptions().apply {
            useViewLifecycleInFragment(true)
        }
        mapFragment = SupportMapFragment.newInstance(options)
        childFragmentManager.beginTransaction().replace(binding.map.id, mapFragment).commit()
        mapFragment.getMapAsync(this)


        binding.eventsRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = earthquakeUIAdapter
        }
        with(viewModel) {
            addPopupMenu(binding.sortButton) {}
            if (!isConnected(requireContext())) {
                makeToast(
                    requireContext(),
                    getString(R.string.no_internet_connection)
                )
            } else {
                viewModel.getLastEarthquakes(requireContext())
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    it.userMessage?.let { msg ->
                        makeToast(requireContext(), msg)
                    }
                    if (it.isLoading) {
                        binding.searchLoading.isVisible = true
                        binding.noDataTV.isVisible = false
                    } else {
                        binding.searchLoading.isVisible = false
                        binding.noDataTV.isVisible = it.data.isEmpty()
                        earthquakeUIAdapter.setEvents(it.data)
                        if (!::map.isInitialized)
                            delay(500)
                        addMarkers(it.data)
                    }
                }

        }

        with(binding) {
            viewModel = this@HomeFragment.viewModel

            searchButton.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
            }

            moreButton.setOnClickListener {
                makeToast(requireContext(), "more")
            }

            BottomSheetBehavior.from(bottomSheet).apply {
                addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        this@HomeFragment.viewModel.bottomSheetState = newState
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }

                })

                state = this@HomeFragment.viewModel.bottomSheetState
            }

        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, currentZoom))
            setOnCameraMoveListener {
                currentPosition = cameraPosition.target
                currentZoom = cameraPosition.zoom
            }
            if (isNightModeOn()) {
                setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext().applicationContext,
                        R.raw.map_night_style
                    )
                )
            }
        }
        setUpCluster()
    }

    private fun setUpCluster() {
        clusterManager = ClusterManager(requireContext(), map)
        with(clusterManager) {
            val renderer =
                ColorClusterRenderer(requireContext(), map, clusterManager)
            this.renderer = renderer
            setOnClusterItemInfoWindowClickListener { event ->
                val e = viewModel.uiState.value.data.firstOrNull {
                    val latitude = it.coordinates?.get(1)
                    val longitude = it.coordinates?.get(0)

                    event.position == LatLng(latitude ?: 0.0, longitude ?: 0.0)
                }
                e?.let {
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToEventDetailsFragment(e)
                    )
                }
            }
        }
        with(map) {
            lifecycleScope.launch {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val hasRequested =
                        datastore.hasLocationRequested()
                    if (!hasRequested) {
                        datastore.setLocationRequested(true)
                        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                } else {
                    datastore.setLocationRequested(false)
                    checkLocationSettings()
                }
            }

            uiSettings.isMapToolbarEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            setOnMarkerClickListener(clusterManager)
            setOnCameraIdleListener(clusterManager)
            setOnInfoWindowClickListener(clusterManager)

            setupCompassButton()
        }
    }

    private fun setupCompassButton() {
        val viewGroup = binding.map.findViewById<ViewGroup>("1".toInt()).parent as ViewGroup
        val compassButton = viewGroup.getChildAt(4)
        /* position compass */
        val compRlp = compassButton.layoutParams as RelativeLayout.LayoutParams
        compRlp.addRule(RelativeLayout.ALIGN_TOP, binding.searchButton.id)
        compRlp.addRule(RelativeLayout.ALIGN_END, binding.searchButton.id)
        compRlp.addRule(RelativeLayout.ALIGN_START, binding.appTitle.id)
        compRlp.setMargins(0, 180, 180, 0)
        compassButton.layoutParams = compRlp
    }

    private fun addMarkers(events: List<EarthquakesUI>) {
        events.forEach { item ->
            val latitude = item.coordinates?.get(1)
            val longitude = item.coordinates?.get(0)
            val time = Date(item.time ?: System.currentTimeMillis()).toTimeString()

            if (latitude != null && longitude != null) {
                val offsetItem =
                    MarkerItem(latitude, longitude, "${item.place} (${item.mag})", time)
                clusterManager.addItem(offsetItem)
            }

        }
        clusterManager.cluster()
    }

    override fun onPause() {
        super.onPause()
        if (::clusterManager.isInitialized) {
            clusterManager.clearItems()
            clusterManager.cluster()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::map.isInitialized) {
            setUpCluster()
        }
    }

    override fun onAvailableNetwork() {}

    override fun onUnavailableNetwork() {
        requireActivity().runOnUiThread {
            binding.map.removeAllViewsInLayout()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNoInternetFragment())
        }

    }

    override fun onLost() {
        requireActivity().runOnUiThread {
            binding.map.removeAllViewsInLayout()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNoInternetFragment())
        }
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_LOW_POWER, 0
        ).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && requestLocation) {
                try {
                    requestLocation = false
                    exception.startResolutionForResult(
                        requireActivity(),
                        ENABLE_LOCATION_REQUEST_CODE
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(
                        "HomeFragment",
                        "Error getting location settings resolution: " + sendEx.message
                    )
                }
            }
        }.addOnSuccessListener { _ ->
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .getCurrentLocation(CurrentLocationRequest.Builder().build(), null)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            try {
                                currentPosition =
                                    LatLng(task.result.latitude, task.result.longitude)
                                map.isMyLocationEnabled = true
                                map.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        currentPosition,
                                        currentZoom
                                    )
                                )
                            } catch (e: NullPointerException) {
                                LogUtils.e("HomeFragment", "${e.message}")
                            }
                        }
                    }

            }
        }
    }

}