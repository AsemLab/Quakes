package com.asemlab.quakes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.asemlab.quakes.databinding.FragmentHomeBinding
import com.asemlab.quakes.ui.models.EQSort
import com.asemlab.quakes.utils.makeToast
import com.asemlab.quakes.utils.slideUpAndFadeIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popupMenu: PopupMenu
    private var earthquakeUIAdapter = EarthquakeUIAdapter(emptyList()){
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEventDetailsFragment(it))
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
        viewModel.getLastEarthquakes(requireContext())

        addPopupMenu()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    it.userMessage?.let { msg ->
                        makeToast(requireContext(), msg)
                    }
                    if (it.isLoading) {
                        makeToast(requireContext(), "Loading...")
                    } else {
                        earthquakeUIAdapter.setEvents(it.data)
                        binding.eventsRV.apply {
                            isVisible = true
                            slideUpAndFadeIn()
                        }
//                        Log.d("TAG", it.data.toString())
                    }
                }

        }

        with(binding) {
            searchButton.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }

            sortButton.setOnClickListener {
                popupMenu.show()
            }

            moreButton.setOnClickListener {
                makeToast(requireContext(), "more")
            }
        }


        return binding.root
    }

    private fun addPopupMenu() {
        popupMenu = PopupMenu(requireContext(), binding.sortButton).apply {
            setOnMenuItemClickListener {
                val descending = popupMenu.menu.findItem(R.id.sortDesc).isChecked
                when (it.itemId) {
                    R.id.sortMag -> viewModel.sortEarthquakes(
                        EQSort.MAG,
                        descending
                    )
                    R.id.sortTime -> viewModel.sortEarthquakes(
                        EQSort.TIME,
                        descending
                    )
                    R.id.sortName -> viewModel.sortEarthquakes(
                        EQSort.NAME,
                        descending
                    )
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