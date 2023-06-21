package com.asemlab.quakes.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asemlab.quakes.R
import com.asemlab.quakes.databinding.FragmentSearchBinding
import com.asemlab.quakes.ui.home.EarthquakeUIAdapter
import com.asemlab.quakes.ui.models.EQSort
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.toSimpleDateFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class SearchFragment : Fragment() {


    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var popupMenu: PopupMenu
    private var earthquakeUIAdapter = EarthquakeUIAdapter(emptyList()) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToEventDetailsFragment(
                it
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        addPopupMenu()
        with(binding) {
            eventsRV.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = earthquakeUIAdapter
            }
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
            sortSpinner.setOnClickListener {
                popupMenu.show()
            }

            regionSpinner.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    resources.getStringArray(R.array.regions)
                )

                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val a = resources.getStringArray(R.array.regions)
                        viewModel.filterByRegion(a[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                }
            }

            val maxDate = Calendar.getInstance()
            val minDate = Calendar.getInstance().apply {
                set(1960, 0, 1)
            }

            val from = Calendar.getInstance()
            val to = Calendar.getInstance()

            fromDate.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    { view, year, month, dayOfMonth ->
                        fromDate.text = "$year-${month + 1}-$dayOfMonth"

                        from.set(year, month, dayOfMonth)

                    },
                    maxDate.get(Calendar.YEAR),
                    maxDate.get(Calendar.MONTH),
                    maxDate.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.maxDate = maxDate.time.time
                    datePicker.minDate = minDate.time.time
                }.show()
            }

            toDate.setOnClickListener {

                DatePickerDialog(
                    requireContext(),
                    { view, year, month, dayOfMonth ->
                        toDate.text = "$year-${month + 1}-$dayOfMonth"
                        to.set(year, month, dayOfMonth)
                    },
                    maxDate.get(Calendar.YEAR),
                    maxDate.get(Calendar.MONTH),
                    maxDate.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.maxDate = maxDate.time.time
                    datePicker.minDate = minDate.time.time
                }.show()
            }

            searchButton.setOnClickListener {

                if (from.timeInMillis == to.timeInMillis) {
                    makeToast(requireContext(), getString(R.string.please_select_a_date))
                    return@setOnClickListener
                }

                fromDate.text = "${from.time.year + 1900}-${from.time.month + 1}-${from.time.date}"
                toDate.text = "${to.time.year + 1900}-${to.time.month + 1}-${to.time.date}"
                startSearch.isVisible = false

                if (from.after(to)) {
                    viewModel.searchEvents(
                        to.time.toSimpleDateFormat(),
                        from.time.toSimpleDateFormat(),
                        binding.magSlider.values[0].toDouble(),
                        binding.magSlider.values[1].toDouble()
                    )
                } else {
                    viewModel.searchEvents(
                        from.time.toSimpleDateFormat(),
                        to.time.toSimpleDateFormat(),
                        binding.magSlider.values[0].toDouble(),
                        binding.magSlider.values[1].toDouble()
                    )
                }
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
                        binding.noResultsTV.isVisible = false
                    } else {
                        binding.searchLoading.isVisible = false
                        earthquakeUIAdapter.setEvents(it.data)
                        binding.noResultsTV.isVisible =
                            it.data.isEmpty() && !binding.startSearch.isVisible
//                        Log.d("TAG", it.data.toString())
                    }
                }


        }

        return binding.root
    }

    private fun addPopupMenu() {
        popupMenu = PopupMenu(requireContext(), binding.sortSpinner).apply {
            setOnMenuItemClickListener {
                val descending = popupMenu.menu.findItem(R.id.sortDesc).isChecked
                when (it.itemId) {
                    R.id.sortMag -> {
                        viewModel.sortEarthquakes(
                            EQSort.MAG,
                            descending
                        )
                        binding.sortSpinner.text = it.title
                    }

                    R.id.sortTime -> {
                        viewModel.sortEarthquakes(
                            EQSort.TIME,
                            descending
                        )
                        binding.sortSpinner.text = it.title
                    }

                    R.id.sortName -> {
                        viewModel.sortEarthquakes(
                            EQSort.NAME,
                            descending
                        )
                        binding.sortSpinner.text = it.title
                    }

                    R.id.sortDesc -> {
                        it.isChecked = !it.isChecked
                        viewModel.sortEarthquakes(
                            descending = it.isChecked
                        )
                    }

                    else -> {}
                }
                return@setOnMenuItemClickListener true
            }
            inflate(R.menu.sort_menu)
        }
    }

}