package com.asemlab.quakes.ui.nointernet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asemlab.quakes.databinding.FragmentNoInternetBinding
import com.asemlab.quakes.utils.isConnected
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoInternetFragment : Fragment() {

    private lateinit var binding: FragmentNoInternetBinding
    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoInternetBinding.inflate(inflater, container, false)

        with(binding) {
            reloadButton.setOnClickListener {
                if (isConnected(requireContext()))
                    findNavController().navigateUp()
            }
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(onBackCallback)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        onBackCallback.remove()
    }

}