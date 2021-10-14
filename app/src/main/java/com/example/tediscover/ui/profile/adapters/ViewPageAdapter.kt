package com.example.tediscover.ui.profile.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.tediscover.ui.profile.fragments.HistoryFragment
import com.example.tediscover.ui.profile.fragments.LikedFragment
import com.example.tediscover.ui.profile.fragments.PlaylistFragment


private const val NUM_TABS = 3

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }


    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return LikedFragment()
            1 -> return HistoryFragment()
        }
        return PlaylistFragment()
    }
}