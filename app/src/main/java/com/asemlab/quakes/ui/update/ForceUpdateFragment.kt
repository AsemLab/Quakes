package com.asemlab.quakes.ui.update

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asemlab.quakes.databinding.FragmentForceUpdateBinding
import com.asemlab.quakes.remote.FirebaseDB
import com.asemlab.quakes.ui.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForceUpdateFragment : Fragment() {

    private lateinit var binding: FragmentForceUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentForceUpdateBinding.inflate(inflater, container, false)

        with(FirebaseDB) {
            forceUpdate.observe(viewLifecycleOwner) { shouldUpdate ->
                shouldUpdate?.let {
                    if (!it)
                        findNavController().navigateUp()
                }
            }
        }

        val updateIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.asemlab.screenbrightness")
        )

        with(binding){
            updateButton.setOnClickListener {
                startActivity(updateIntent)
            }
        }

        return binding.root
    }

}