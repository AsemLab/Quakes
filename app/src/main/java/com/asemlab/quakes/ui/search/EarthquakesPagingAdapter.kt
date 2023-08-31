package com.asemlab.quakes.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asemlab.quakes.databinding.QuakeItemBinding
import com.asemlab.quakes.ui.models.EarthquakesUI

class EarthquakesPagingAdapter(
    private val onClick: (EarthquakesUI) -> Unit
) :
    PagingDataAdapter<EarthquakesUI, EarthquakesPagingAdapter.EarthquakeViewHolder>(EarthquakeDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeViewHolder {
        return EarthquakeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EarthquakeViewHolder, position: Int) {
        holder.bind(getItem(position)!!, onClick)
    }


    class EarthquakeViewHolder private constructor(private val binding: QuakeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): EarthquakeViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return EarthquakeViewHolder(QuakeItemBinding.inflate(inflater, parent, false))
            }
        }

        fun bind(event: EarthquakesUI, onClick: (EarthquakesUI) -> Unit) {
            with(binding) {
                this.event = event
                mainItemContainer.setOnClickListener {
                    onClick(event)
                }
            }
        }
    }

    companion object {
        private val EarthquakeDiff = object : DiffUtil.ItemCallback<EarthquakesUI>() {
            override fun areItemsTheSame(
                oldItem: EarthquakesUI,
                newItem: EarthquakesUI
            ) = oldItem.rowId == newItem.rowId

            override fun areContentsTheSame(
                oldItem: EarthquakesUI,
                newItem: EarthquakesUI
            ) =
                oldItem.id == newItem.id && oldItem.rowId == newItem.rowId && oldItem.place == newItem.place

        }

    }

}