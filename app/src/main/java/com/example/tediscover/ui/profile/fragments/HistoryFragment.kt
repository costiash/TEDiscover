package com.example.tediscover.ui.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentHistoryBinding
import com.example.tediscover.ui.profile.adapters.HistoryItemAdapter
import com.example.tediscover.viewmodels.FirebaseViewModel


class HistoryFragment : Fragment() {

    private val sharedViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding: FragmentHistoryBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        binding.viewModel = sharedViewModel
        binding.historyVideosRecyclerView.adapter = HistoryItemAdapter()

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).ensureBottomNavigation()
    }
}