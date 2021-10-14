package com.example.tediscover.ui.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentLikedBinding
import com.example.tediscover.ui.profile.adapters.ProfileItemAdapter
import com.example.tediscover.viewmodels.FirebaseViewModel

class LikedFragment : Fragment() {

    private val sharedViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding: FragmentLikedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikedBinding.inflate(inflater, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = sharedViewModel
        binding.likedVideosRecyclerView.adapter = ProfileItemAdapter()

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).ensureBottomNavigation()
    }
}