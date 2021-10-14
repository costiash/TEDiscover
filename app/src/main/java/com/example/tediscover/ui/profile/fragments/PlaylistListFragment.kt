package com.example.tediscover.ui.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentPlaylistListBinding
import com.example.tediscover.ui.models.EventItem
import com.example.tediscover.ui.models.TalkItem
import com.example.tediscover.ui.profile.adapters.PlaylistListAdapter
import com.example.tediscover.viewmodels.FirebaseViewModel
import androidx.navigation.fragment.findNavController


class PlaylistListFragment : Fragment(), PlaylistListAdapter.OnClickListener {

    private lateinit var binding: FragmentPlaylistListBinding
    private val sharedViewModel: FirebaseViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistListBinding.inflate(layoutInflater, container, false)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        binding.viewModel = sharedViewModel

        binding.playlistListRecyclerView.adapter = PlaylistListAdapter(this)
        (activity as TedActivity).supportActionBar!!.title = sharedViewModel.currentPlaylist.value?.playlistName

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observes when the playlist items changed.
        sharedViewModel.currentPlaylist.observe(viewLifecycleOwner, Observer {
            binding.playlistListRecyclerView.adapter!!.notifyDataSetChanged()
        })
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).ensureBottomNavigation()
    }


    override fun onItemClick(eventItem: EventItem) {
        val action = PlaylistListFragmentDirections.actionPlaylistListFragmentToVideoDetailFragment(eventItem)
        findNavController().navigate(action)
    }


    override fun onBtnMoreClick(btn: View, talkItem: TalkItem) {
        showPlaylistListMenu(btn, R.menu.popup_menu, talkItem, sharedViewModel.currentPlaylist.value?.playlistName!!)
    }


    private fun showPlaylistListMenu(v: View, @MenuRes menuRes: Int, talkItem: TalkItem, playlistName: String) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            if (menuItem.itemId == R.id.removeItem) {
                sharedViewModel.removeFromList(talkItem)
                sharedViewModel.removeItemFromPlaylist(playlistName, talkItem)
            }
            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}