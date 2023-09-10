package com.asemlab.quakes.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asemlab.quakes.databinding.QuakeItemBinding
import com.asemlab.quakes.model.EarthquakesUI

class EarthquakeUIAdapter(
    private var events: List<EarthquakesUI>,
    private val onClick: (EarthquakesUI) -> Unit
) :
    RecyclerView.Adapter<EarthquakeUIAdapter.EarthquakeVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarthquakeVH {
        return EarthquakeVH.from(parent)
    }

    override fun getItemCount() = events.size

    fun setEvents(events: List<EarthquakesUI>) {
        this.events = events
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EarthquakeVH, position: Int) {
        holder.bind(events[position], onClick)
    }

    class EarthquakeVH private constructor(private val binding: QuakeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): EarthquakeVH {
                val inflater = LayoutInflater.from(parent.context)
                return EarthquakeVH(QuakeItemBinding.inflate(inflater, parent, false))
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
}