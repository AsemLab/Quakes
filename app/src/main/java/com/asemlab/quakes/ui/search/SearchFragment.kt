package com.asemlab.quakes.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        with(binding) {
            viewModel = this@SearchFragment.viewModel
            eventsRV.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = earthquakeUIAdapter
            }
            nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (scrollY == 0) {
                    searchButton.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            transitionAlpha = 1f
                        } else {
                            alpha = 1f
                        }
                        extend()
                    }
                } else {
                    searchButton.apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            transitionAlpha = .5f
                        } else {
                            alpha = .5f
                        }
                        shrink()
                    }
                }
            }
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }

            regionSpinner.apply {
                adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.region_spinner_item,
                    resources.getStringArray(R.array.regions)
                )
            }

            magSlider.addOnChangeListener { slider, value, fromUser ->
                viewModel?.onMagSliderChanged(slider, value, fromUser)
            }

        }
        viewModel.apply {
            addPopupMenu(binding.sortSpinner){
                sortText.postValue(it)
            }
            showStartSearch.observe(viewLifecycleOwner) {
                binding.startSearch.isVisible = it
            }
            fromDate.observe(viewLifecycleOwner) {
                binding.fromDate.text = it.ifEmpty { getString(R.string.from_title) }
            }
            toDate.observe(viewLifecycleOwner) {
                binding.toDate.text = it.ifEmpty { getString(R.string.to_title) }
            }
            sortText.observe(viewLifecycleOwner) {
                binding.sortSpinner.text = it.ifEmpty { getString(R.string.time) }
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


}