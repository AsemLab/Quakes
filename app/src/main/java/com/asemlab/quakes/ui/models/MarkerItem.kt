package com.asemlab.quakes.ui.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


data class MarkerItem(
    val lat: Double,
    val lng: Double,
    val markerTitle: String,
    val markerSnippet: String
) : ClusterItem {

    private val position: LatLng = LatLng(lat, lng)

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return markerTitle
    }

    override fun getSnippet(): String {
        return markerSnippet
    }

    override fun getZIndex(): Float? {
        return 0f
    }

}

