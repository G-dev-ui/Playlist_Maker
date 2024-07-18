package com.example.playlist_maker.music_library.ui.playlist


import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.playlist_maker.R
import com.example.playlist_maker.databinding.FragmentNewPlaylistBinding
import com.example.playlist_maker.main.ui.MainActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


open class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!
    private var coverUriSelect: Uri? = null
    private var showedDialog: Boolean = false
    private val vm by viewModel<NewPlaylistViewModel>()
    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.hideNavBar()

        vm.savedCoverUri.observe(viewLifecycleOwner) { savedUri ->
            coverUriSelect = savedUri
        }

        val chooseCover =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { previewUri ->
                if (previewUri != null) {
                    vm.saveCoverToStorage(previewUri, requireContext())
                    binding.newPlaylistCover.setImageURI(previewUri)
                    showedDialog = true
                } else {

                }
            }

        binding.newPlaylistCover.setOnClickListener {
            chooseCover.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
        }

        binding.createButton.setOnClickListener {
            newPlaylistAdd(coverUriSelect)
            Toast.makeText(
                requireContext(),
                "Плейлист ${binding.newPlaylistNameEditTxt.text.toString()} создан",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }

        binding.newPlaylistNameEditTxt.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                val color = ContextCompat.getColor(requireContext(), R.color.background_main)
                showedDialog = true
                binding.createButton.isEnabled = true
                binding.createButton.setBackgroundColor(color)
            } else {
                val color = ContextCompat.getColor(requireContext(), R.color.gray1)
                binding.createButton.isEnabled = false
                binding.createButton.setBackgroundColor(color)
            }

        }

        binding.toolbarNewPlaylist.setNavigationOnClickListener  {
            if (showedDialog) {
                showDialog()
            } else {
                findNavController().navigateUp()
                (activity as? MainActivity)?.showNavBar()
            }
        }

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (showedDialog) {
                    showDialog()
                } else {
                    (activity as? MainActivity)?.showNavBar()
                    findNavController().navigateUp()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.custom_alert_dialog_theme)
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setNegativeButton(getString(R.string.dialog_negative_btn)) { dialog, wich ->
            }
            .setPositiveButton(getString(R.string.dialog_positive_btn)) { dialog, witch ->
                findNavController().navigateUp()
                (activity as? MainActivity)?.showNavBar()

            }
            .show()
    }
    private fun newPlaylistAdd(coverUri: Uri?) {
        val playlistName = binding.newPlaylistNameEditTxt.text.toString()
        val playlistDescription = binding.newPlaylistDescriptionEditTxt.text.toString()
        vm.newPlaylist(playlistName, playlistDescription, coverUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::callback.isInitialized) {
            callback.remove()
        }
    }
}