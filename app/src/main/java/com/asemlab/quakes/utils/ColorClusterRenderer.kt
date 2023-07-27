package com.asemlab.quakes.utils

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.asemlab.quakes.R
import com.asemlab.quakes.ui.models.MarkerItem
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager

import com.google.maps.android.clustering.view.DefaultClusterRenderer


class ColorClusterRenderer(
    val context: Context,
    map: GoogleMap, clusterManager: ClusterManager<MarkerItem>
) : DefaultClusterRenderer<MarkerItem>(context, map, clusterManager) {

    override fun getColor(clusterSize: Int): Int {
        return ResourcesCompat.getColor(context.resources, R.color.cluster, null)
    }

}