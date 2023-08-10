package com.asemlab.quakes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.asemlab.quakes.remote.FirebaseDB
import com.asemlab.quakes.ui.models.EarthquakesUI
import com.asemlab.quakes.ui.models.MarkerItem
import com.asemlab.quakes.utils.ColorClusterRenderer
import com.asemlab.quakes.utils.isConnected
import com.asemlab.quakes.utils.isNightModeOn
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.toTimeString
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
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
    private var earthquakeUIAdapter = EarthquakeUIAdapter(emptyList()) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToEventDetailsFragment(
                it
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.eventsRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = earthquakeUIAdapter
        }
        with(viewModel) {
            addPopupMenu(binding.sortButton) {}
            if (!isConnected(requireContext())) {
                makeToast(requireContext(), getString(R.string.no_internet_connection))
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
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
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

        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        with(FirebaseDB) {
            forceUpdate.observe(viewLifecycleOwner) { shouldUpdate ->
                shouldUpdate?.let {
                    if (it)
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToForceUpdateFragment())
                }
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
        setUpClusterer()
    }

    private fun setUpClusterer() {
        clusterManager = ClusterManager(requireContext(), map)
        with(clusterManager) {
            val renderer = ColorClusterRenderer(requireContext(), map, clusterManager)
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
            setOnMarkerClickListener(clusterManager)
            setOnCameraIdleListener(clusterManager)
            setOnInfoWindowClickListener(clusterManager)
        }
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
            setUpClusterer()
        }
    }

    override fun onAvailableNetwork() {
        viewModel.getLastEarthquakes(requireContext())
    }

    override fun onUnavailableNetwork() {
        makeToast(requireContext(), "UnAvailable")

    }

    override fun onLost() {
        makeToast(requireContext(), "OnLost")
    }

}