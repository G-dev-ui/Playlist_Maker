package com.example.playlist_maker.music_library.ui.playlist

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.playlist_maker.R
import com.example.playlist_maker.main.ui.MainActivity
import com.example.playlist_maker.music_library.domain.Playlist
import com.example.playlist_maker.util.MODIFY_PLAYLIST
import org.koin.androidx.viewmodel.ext.android.viewModel

class ModifyPlaylistFragment : NewPlaylistFragment() {
    private val vm by viewModel<ModifyPlaylistViewModel>()
    private var playlist: Playlist? = null
    private var coverUriSelect: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.hideNavBar()

        playlist = arguments?.getParcelable(MODIFY_PLAYLIST) as? Playlist

        val chooseCover =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { previewUri ->
                if (previewUri != null) {
                    vm.saveCoverToStorage(previewUri, requireContext())
                    binding.newPlaylistCover.setImageURI(previewUri)
                } else {
                    Log.d("PlaylistCover", "User did not select an image")
                }
            }

        binding.toolbarNewPlaylist.title = getString(R.string.edit_modify)

        binding.createButton.text = getString(R.string.save_modify)

        binding.newPlaylistCover.setOnClickListener {
            chooseCover.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
        }

        binding.toolbarNewPlaylist.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        vm.savedCoverUri.observe(viewLifecycleOwner, Observer { uri ->
            coverUriSelect = uri
        })

        vm.playlist.observe(viewLifecycleOwner) { playlist ->
            vm.getPlaylist(playlist)
        }

        binding.createButton.setOnClickListener {
            playlist.let { playlist ->
                modifyPlaylist(coverUriSelect, playlist!!)
                findNavController().popBackStack()
            }
        }

        playlist.let { playlist ->
            binding.newPlaylistNameEditTxt.setText(playlist?.name)
            binding.newPlaylistDescriptionEditTxt.setText(playlist?.description)
            if (!playlist?.imageUri.isNullOrEmpty()) {
                val imageUri = Uri.parse(playlist?.imageUri)
                coverUriSelect = imageUri
                binding.newPlaylistCover.setImageURI(imageUri)
            }
        }
    }

    private fun modifyPlaylist(coverUri: Uri?, originalPlayList: Playlist) {
        val name = binding.newPlaylistNameEditTxt.text.toString()
        val description = binding.newPlaylistDescriptionEditTxt.text.toString()
        val cover = vm.getCover().toString()
        vm.modifyData(name, description, cover, coverUri, originalPlayList)
    }
}