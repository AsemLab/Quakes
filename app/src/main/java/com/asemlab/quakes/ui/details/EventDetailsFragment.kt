package com.asemlab.quakes.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.asemlab.quakes.R
import com.asemlab.quakes.databinding.FragmentEventDetailsBinding
import com.asemlab.quakes.utils.isNightModeOn
import com.asemlab.quakes.utils.slideDown
import com.asemlab.quakes.utils.slideDownAndFadeOut
import com.asemlab.quakes.utils.slideUp
import com.asemlab.quakes.utils.slideUpAndFadeIn
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val viewModel by viewModels<EventDetailsViewModel>()
    private val args: EventDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentEventDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)

        with(binding) {
            event = args.event
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
            fullscreenButton.setOnClickListener {
                if (detailsContainer.alpha > 0) {
                    detailsContainer.slideDownAndFadeOut(detailsContainer.height)
                    fullscreenButton.slideDown(detailsContainer.height - fullscreenButton.height - 50)
                    fullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit)
                } else {
                    detailsContainer.slideUpAndFadeIn()
                    fullscreenButton.slideUp()
                    fullscreenButton.setImageResource(R.drawable.ic_fullscreen)
                }

            }
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val latitude = args.event?.coordinates?.get(1)!!
        val longitude = args.event?.coordinates?.get(0)!!
        val zoom = 5f

        val eventPosition = LatLng(latitude, longitude)

        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(eventPosition, zoom))
            addMarker(MarkerOptions().position(eventPosition))
            if (isNightModeOn()) {
                setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext().applicationContext,
                        R.raw.map_night_style
                    )
                )
            }
        }
    }
}