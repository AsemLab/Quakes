package com.asemlab.quakes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asemlab.quakes.R
import com.asemlab.quakes.databinding.FragmentHomeBinding
import com.asemlab.quakes.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private var earthquakeUIAdapter = EarthquakeUIAdapter(emptyList()) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToEventDetailsFragment(
                it
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.eventsRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = earthquakeUIAdapter
        }
        with(viewModel) {
            addPopupMenu(binding.sortButton) {}
            getLastEarthquakes(requireContext())
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                .collect {
                    it.userMessage?.let { msg ->
                        makeToast(requireContext(), msg)
                    }
                    if (it.isLoading) {
                        binding.searchLoading.isVisible = true
                    } else {
                        binding.searchLoading.isVisible = false
                        earthquakeUIAdapter.setEvents(it.data)
//                        Log.d("TAG", it.data.toString())
                    }
                }

        }

        with(binding) {
            viewModel = this@HomeFragment.viewModel

            searchButton.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }

            moreButton.setOnClickListener {
                makeToast(requireContext(), "more")
            }
        }


        return binding.root
    }


}