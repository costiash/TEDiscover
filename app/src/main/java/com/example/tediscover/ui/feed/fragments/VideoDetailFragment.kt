package com.example.tediscover.ui.feed.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.load
import com.example.tediscover.ModalBottomSheet
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentVideoDetailBinding
import com.example.tediscover.ui.feed.VideoActivity
import com.example.tediscover.ui.models.EventItem
import com.example.tediscover.ui.models.TalkItem
import com.example.tediscover.viewmodels.FirebaseViewModel
import com.example.tediscover.viewmodels.RemoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class VideoDetailFragment : Fragment() {

    private val sharedViewModel: FirebaseViewModel by activityViewModels()
    private lateinit var binding: FragmentVideoDetailBinding
    private lateinit var fab: FloatingActionButton
    private val viewModel: RemoteViewModel by viewModels()
    private var videoUri: String? = null
    private lateinit var item: EventItem
    private var talkItem: TalkItem? = null
    private var liked = false
    private var inPlaylist = false
    private var playlistName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as TedActivity).ensureToolBar()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoDetailBinding.inflate(inflater, container, false)

        binding.progressLayout.visibility = View.VISIBLE
        val args = VideoDetailFragmentArgs.fromBundle(requireArguments())
        item = args.tedItem!!

        fab = (activity as TedActivity).findViewById(R.id.my_fab)

        binding.titleTextView.text = item.title
        binding.speakerTextView.text = item.speaker
        binding.dateTextView.text = item.date

        val imgUri = item.thumbImage.toUri().buildUpon().scheme("https").build()
        binding.talkCover.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }

        viewModel.fetchEvent(item.talkUrl).observe(viewLifecycleOwner, Observer {
            if (it.videoUrl.isBlank()){
                binding.progressLayout.visibility = View.GONE
                binding.errorTextView.visibility = View.VISIBLE
                binding.errorLayout.visibility = View.VISIBLE
            } else {
                binding.tagsTextView.text = it.tags.joinToString(", ")
                binding.descriptionTextView.text = it.description
                videoUri = it.videoUrl
                binding.progressLayout.visibility = View.GONE
                fab.visibility = View.VISIBLE
                createTalkItem()
                fab.setOnClickListener {
                    sharedViewModel.addHistoryVideo(talkItem!!)
                    val intent = Intent(requireContext(), VideoActivity::class.java)
                    intent.putExtra("LINK", videoUri)
                    requireContext().startActivity(intent)
                }
                liked = sharedViewModel.checkIfLiked(talkItem!!.id)
                inPlaylist = sharedViewModel.checkIfInPlaylist(talkItem!!)
                if (liked) {
                    binding.likeBtn.setImageDrawable(ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_like_active))
                }

                if (inPlaylist) {
                    playlistName = sharedViewModel.markPlaylistAsChecked(talkItem!!)
                    binding.addToPlaylistBtn.setImageDrawable(ContextCompat
                        .getDrawable(requireContext(), R.drawable.ic_playlist_add_active))
                }
            }
        })
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.playlists.observe(viewLifecycleOwner, Observer {
            sharedViewModel.playlistData.value?.forEach {
                val playlist = it.value
                if (talkItem != null) {
                    if (talkItem in playlist) {
                        playlistName = it.key
                        inPlaylist = true
                        binding.addToPlaylistBtn.setImageDrawable(ContextCompat
                            .getDrawable(requireContext(), R.drawable.ic_playlist_add_active))
                    }
                }

            }
        })

        sharedViewModel.isItemRemovedFromPlaylist.observe(viewLifecycleOwner, Observer {
            if (sharedViewModel.isItemRemovedFromPlaylist.value!!) {
                playlistName = ""
                inPlaylist = false
                binding.addToPlaylistBtn.setImageDrawable(ContextCompat
                    .getDrawable(requireContext(), R.drawable.ic_playlist_add))
            }
        })

        sharedViewModel.newPlaylistName.observe(viewLifecycleOwner, Observer {
            if (!sharedViewModel.newPlaylistName.value.isNullOrBlank()) {
                binding.addToPlaylistBtn.setImageDrawable(ContextCompat
                    .getDrawable(requireContext(), R.drawable.ic_playlist_add_active))
                playlistName = it
                inPlaylist = true
            }
        })

        binding.likeBtn.setOnClickListener {
            liked = if (!liked) {
                binding.likeBtn.setImageDrawable(ContextCompat
                    .getDrawable(requireContext(), R.drawable.ic_like_active))
                sharedViewModel.addNewLikeVideo(talkItem!!)
                Snackbar.make(requireView(), R.string.item_liked, Snackbar.LENGTH_LONG)
                    .setAnchorView(fab).show()
                true
            } else {
                binding.likeBtn.setImageDrawable(ContextCompat
                    .getDrawable(requireContext(), R.drawable.ic_like))
                sharedViewModel.removeLikeVideo(talkItem!!)
                Snackbar.make(requireView(), R.string.item_not_liked, Snackbar.LENGTH_LONG)
                    .setAnchorView(fab).show()
                false
            }
        }
    }


    override fun onStart() {
        super.onStart()
        binding.addToPlaylistBtn.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet(talkItem!!, playlistName)
            modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)
        }
    }

    private fun createTalkItem() {
        val tags: List<String> = listOf(*binding.tagsTextView.text.split(", ")
            .toTypedArray())

        val strForId = videoUri!!
        val splitStrForId = strForId.split("/")

        val videoId: String = if (splitStrForId.size == 6) {
            splitStrForId[4]
        } else {
            item.id.toString()
        }

        talkItem = TalkItem(videoId, item.thumbImage, item.duration,
            item.speaker, item.title, item.date, tags as MutableList<String>,
            binding.descriptionTextView.text.toString(), item.talkUrl)
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).ensureBottomNavigation()
        if (videoUri != null)
            fab.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        fab.visibility = View.INVISIBLE
        sharedViewModel.resetNewPlaylistName()
        (activity as TedActivity).makeScroll()
        playlistName = ""
    }
}