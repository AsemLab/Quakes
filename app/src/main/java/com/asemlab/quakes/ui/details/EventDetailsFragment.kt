package com.asemlab.quakes.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.asemlab.quakes.R
import com.asemlab.quakes.databinding.FragmentEventDetailsBinding
import com.asemlab.quakes.utils.slideDown
import com.asemlab.quakes.utils.slideDownAndFadeOut
import com.asemlab.quakes.utils.slideUp
import com.asemlab.quakes.utils.slideUpAndFadeIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventDetailsFragment : Fragment() {


    private val viewModel by viewModels<EventDetailsViewModel>()
    private val args: EventDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentEventDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailsBinding.inflate(inflater, container, false)

        with(binding) {
            event = args.event
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
            fullscreenButton.setOnClickListener {
                if (detailsContainer.alpha > 0) {
                    detailsContainer.slideDownAndFadeOut(detailsContainer.height)
                    fullscreenButton.slideDown(detailsContainer.height - fullscreenButton.height)
                    fullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit)
                }
                else {
                    detailsContainer.slideUpAndFadeIn()
                    fullscreenButton.slideUp()
                    fullscreenButton.setImageResource(R.drawable.ic_fullscreen)
                }

            }
        }


        return binding.root
    }

}