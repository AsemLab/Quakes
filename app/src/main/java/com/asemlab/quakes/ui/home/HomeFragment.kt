package com.asemlab.quakes.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.asemlab.quakes.R
import com.asemlab.quakes.databinding.FragmentHomeBinding
import com.asemlab.quakes.ui.models.EQSort
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popupMenu: PopupMenu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.getLastEarthquakes(requireContext())

        addPopupMenu()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    it.userMessage?.let { msg ->
                        makeToast(msg)
                    }
                    if (it.isLoading) {
                        makeToast("Loading...")
                    } else {
                        Log.d("TAG", it.data.toString())
                    }
                }

        }

        with(binding) {
            searchButton.setOnClickListener {
                makeToast("search")
            }

            sortButton.setOnClickListener {
                popupMenu.show()
            }

            moreButton.setOnClickListener {
                makeToast("more")
            }
        }


        return binding.root
    }

    private fun addPopupMenu() {
        popupMenu = PopupMenu(requireContext(), binding.sortButton).apply {
            setOnMenuItemClickListener {
                val descending = popupMenu.menu.findItem(R.id.descMenu).isChecked
                when (it.itemId) {
                    R.id.magMenu -> viewModel.sortEarthquakes(
                        EQSort.MAG,
                        descending
                    )
                    R.id.timeMenu -> viewModel.sortEarthquakes(
                        EQSort.TIME,
                        descending
                    )
                    R.id.regionMenu -> viewModel.sortEarthquakes(
                        EQSort.REGION,
                        descending
                    )
                    R.id.descMenu -> {
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

    private fun makeToast(m: String) {
        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show()
    }

}