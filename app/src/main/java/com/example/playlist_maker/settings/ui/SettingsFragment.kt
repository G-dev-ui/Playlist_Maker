package com.example.playlist_maker.settings.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentSettingsBinding
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {


     private  var _binding: FragmentSettingsBinding? = null
    private lateinit var themeSwitcher: SwitchMaterial

    private val binding get() = _binding!!

     private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeSwitcher = binding.root.findViewById(R.id.theme_switch)

        settingsViewModel.getThemeLiveData().observe(viewLifecycleOwner) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        binding.shareTheApplication.setOnClickListener {
            settingsViewModel.ShareApp()
        }

        binding.writeToSupport.setOnClickListener {
            settingsViewModel.OpenSupport()
        }

        binding.userAgreement.setOnClickListener {
            settingsViewModel.OpenTerms()
        }


        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.clickSwitchTheme(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}