package com.example.tediscover.ui.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentPlaylistBinding
import com.example.tediscover.ui.models.PlaylistData
import com.example.tediscover.ui.profile.adapters.PlaylistAdapter
import com.example.tediscover.viewmodels.FirebaseViewModel


class PlaylistFragment : Fragment(), PlaylistAdapter.OnClickListener {

    private val sharedViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding:FragmentPlaylistBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = sharedViewModel
        binding.playlistsRecyclerView.adapter = PlaylistAdapter(requireContext(), this)

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).ensureBottomNavigation()
    }


    override fun onItemClick(playlistData: PlaylistData) {
        sharedViewModel.setPlaylist(playlistData.playlistName)
        val action = ProfileDirections.actionNavigationProfileToPlaylistListFragment()
        findNavController().navigate(action)
    }
}