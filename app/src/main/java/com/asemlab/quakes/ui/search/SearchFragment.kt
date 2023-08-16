package com.asemlab.quakes.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
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
import com.asemlab.quakes.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {


    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var binding: FragmentSearchBinding
    private var earthquakeUIAdapter = EarthquakeUIAdapter(emptyList()) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToEventDetailsFragment(it)
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        with(binding) {
            lifecycleOwner = this@SearchFragment
            viewModel = this@SearchFragment.viewModel
            eventsRV.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = earthquakeUIAdapter
            }
            nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY == 0) {
                    searchButton.apply {
                        icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_search, null)
                        extend()
                        setOnClickListener(this@SearchFragment.viewModel::onSearch)
                    }
                } else {
                    searchButton.apply {
                        icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow_up, null)
                        shrink()
                        setOnClickListener {
                            nestedScrollView.smoothScrollTo(0, 0)
                        }
                    }
                }
            }
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }


            magSlider.addOnChangeListener { slider, value, fromUser ->
                viewModel?.onMagSliderChanged(slider, value, fromUser)
            }

            dateRange.setOnClickListener {
                this@SearchFragment.viewModel.rangePicker.show(childFragmentManager, null)
            }

        }
        viewModel.apply {
            initializeDateRangePicker(requireContext())

            addPopupMenu(binding.sortSpinner) {
                sortText.postValue(it)
            }

            addRegionPopupMenu(binding.regionSpinner) {
                region.postValue(it)
            }

            showStartSearch.observe(viewLifecycleOwner) {
                binding.startSearch.isVisible = it
            }
            fromDateText.observe(viewLifecycleOwner) {
                binding.fromTitle.isVisible = it.isNotEmpty()
                binding.toTitle.isVisible = it.isNotEmpty()
                val from = it.ifEmpty { getString(R.string.from_title) }
                val to = toDateText.value?.ifEmpty { getString(R.string.to_title) }

                binding.dateRange.text = getString(R.string.from_to, from, to)
            }
            toDateText.observe(viewLifecycleOwner) {
                binding.fromTitle.isVisible = it.isNotEmpty()
                binding.toTitle.isVisible = it.isNotEmpty()
                val from = fromDateText.value?.ifEmpty { getString(R.string.from_title) }
                val to = it.ifEmpty { getString(R.string.to_title) }

                binding.dateRange.text = getString(R.string.from_to, from, to)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED).collect {
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
                }
            }
        }
        return binding.root
    }


}