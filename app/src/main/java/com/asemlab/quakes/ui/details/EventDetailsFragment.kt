package com.asemlab.quakes.ui.details

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.asemlab.quakes.BuildConfig
import com.asemlab.quakes.R
import com.asemlab.quakes.databinding.FragmentEventDetailsBinding
import com.asemlab.quakes.utils.isNightModeOn
import com.blankj.utilcode.util.LogUtils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val viewModel by viewModels<EventDetailsViewModel>()
    private val args: EventDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentEventDetailsBinding
    private lateinit var adRequest: AdRequest
    private var interstitialAd: InterstitialAd? = null
    private lateinit var bannerAd: AdView
    private lateinit var consentInformation: ConsentInformation


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
            detailsBottomSheet?.let {
                BottomSheetBehavior.from(it).apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            consentInformation = UserMessagingPlatform.getConsentInformation(requireContext())
            if (consentInformation.canRequestAds()) {
                loadBannerAd()
                loadInterstitialAd()
            }
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
    }

    private fun loadBannerAd() {
        adRequest = AdRequest.Builder().build()
        bannerAd = AdView(requireContext()).apply {
            adUnitId = BuildConfig.BANNER_ID
            setAdSize(AdSize.BANNER)
        }
        with(binding.adView) {
            addView(bannerAd)
        }
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            requireContext(),
            BuildConfig.INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    LogUtils.d(adError.toString())
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    LogUtils.d("Ad was loaded.")
                    this@EventDetailsFragment.interstitialAd = interstitialAd
                    this@EventDetailsFragment.interstitialAd!!.show(requireActivity())
                }
            })

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                LogUtils.d("Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                LogUtils.d("Ad dismissed fullscreen content.")
                interstitialAd = null
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                LogUtils.e("Ad failed to show fullscreen content.")
                interstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                LogUtils.d("Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                LogUtils.d("Ad showed fullscreen content.")
            }
        }
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
            uiSettings.isMapToolbarEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                LocationServices.getFusedLocationProviderClient(requireContext())
                    .getCurrentLocation(CurrentLocationRequest.Builder().build(), null)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            isMyLocationEnabled = true
                        }
                    }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::bannerAd.isInitialized)
            bannerAd.loadAd(adRequest)
    }
}