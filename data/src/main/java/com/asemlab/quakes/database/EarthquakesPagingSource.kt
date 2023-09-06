package com.asemlab.quakes.database

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.asemlab.quakes.database.models.EQSort
import com.asemlab.quakes.database.models.EarthquakesUI
import com.asemlab.quakes.database.models.findCountryByEventTitle
import com.asemlab.quakes.remote.repositories.EarthquakeManager
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class SearchQuakesPagingSource(
    private val earthquakeManager: EarthquakeManager,
    private val startTime: String,
    private val endTime: String,
    private val minMagnitude: Double,
    private val maxMagnitude: Double,
    private val onError: (String?) -> Unit,
    private val region: String,
    private val orderBy: EQSort
) : PagingSource<Int, EarthquakesUI>() {

    override fun getRefreshKey(state: PagingState<Int, EarthquakesUI>): Int? {
        val position = state.anchorPosition ?: return null
        val closestItem = state.closestItemToPosition(position) ?: return null
        val key = closestItem.rowId?.minus(state.config.pageSize / 2) ?: 0
        LogUtils.d(Integer.max(key.toInt(), 0))
        return Integer.max(key.toInt(), 0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EarthquakesUI> {
        val position = params.key ?: 0
        val load = CoroutineScope(Dispatchers.IO).async {
            try {
                val query = earthquakeManager.getOrderByQuery(region, orderBy, position)
                var result = earthquakeManager.getEarthquakesUIOffset(query)
                val dataSize = earthquakeManager.getEarthquakesUISize()
                if (dataSize == 0) {
                    val earthquakeData = async {
                        earthquakeManager.getEarthquakesByMag(
                            startTime,
                            endTime,
                            minMagnitude,
                            maxMagnitude,
                            {},
                            onError
                        )
                    }.await()
                    earthquakeManager.insertEarthquakes(
                        earthquakeData.map {
                            it.findCountryByEventTitle(
                                earthquakeManager.getStates(),
                                earthquakeManager.getCountries()
                            )
                        }
                    )

                }
                result = earthquakeManager.getEarthquakesUIOffset(query)

                LoadResult.Page(
                    data = result,
                    prevKey = if (position == 0) null else position - com.asemlab.quakes.utils.DEFAULT_PAGE_SIZE,
                    nextKey = if (result.isEmpty()) null else position + com.asemlab.quakes.utils.DEFAULT_PAGE_SIZE
                )
            } catch (e: Exception) {
                LogUtils.e("SearchQuakesPaging", e.message ?: "")
                LoadResult.Error(e)
            }
        }.await()

        return load
    }
}