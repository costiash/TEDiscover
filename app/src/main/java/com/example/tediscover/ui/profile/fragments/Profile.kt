package com.example.tediscover.ui.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentProfileBinding
import com.example.tediscover.ui.profile.adapters.ViewPagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator

val tabsArray = arrayOf(
    "Liked",
    "History",
    "Playlist"
)

class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting the tool bar title to contain the user's name in it
        setUserNameTitle()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        (activity as TedActivity).findViewById<FloatingActionButton>(R.id.my_fab).visibility = View.INVISIBLE
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val iconsArray = arrayOf(
            ResourcesCompat.getDrawable(this.resources, R.drawable.ic_like, null),
            ResourcesCompat.getDrawable(this.resources, R.drawable.ic_history, null),
            ResourcesCompat.getDrawable(this.resources, R.drawable.ic_playlist, null)
        )

        binding.apply {
            val viewPager = viewPager
            val tabLayout = tabLayout

            val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabsArray[position]
                tab.icon = iconsArray[position]
            }.attach()
        }
    }


    override fun onResume() {
        super.onResume()
        setUserNameTitle()
    }


    private fun setUserNameTitle() {
        val userName = (activity as TedActivity).userName
        (activity as TedActivity).supportActionBar!!.title = "$userName's Profile"
        (activity as TedActivity).supportActionBar!!.elevation = 0f
        (activity as TedActivity).makeScroll()
    }
}