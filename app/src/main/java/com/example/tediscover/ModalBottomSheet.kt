package com.example.tediscover

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.tediscover.databinding.BottomSheetContentBinding
import com.example.tediscover.ui.models.TalkItem
import com.example.tediscover.viewmodels.FirebaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ModalBottomSheet(
    private val talkItem: TalkItem,
    private val playListName: String
    ) : BottomSheetDialogFragment() {


    companion object {
        const val TAG = "ModalBottomSheet"
    }


    private lateinit var binding: BottomSheetContentBinding
    private val sharedViewModel: FirebaseViewModel by activityViewModels()
    private var newPlaylistName = ""
    private var checkGroup: ViewGroup? = null
    private var currPlaylistName = playListName


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkGroup = binding.playlistLinearLayout

        sharedViewModel.playlists.observe(viewLifecycleOwner, Observer {
            if (sharedViewModel.playlists.value!!.isNotEmpty()) {
                for (playlist in sharedViewModel.playlists.value!!) {
                    if (!checkIfPlaylistExist(playlist.playlistName, checkGroup!!)) {
                        val playlistName = layoutInflater.inflate(R.layout.check_layout, checkGroup,
                            false) as CheckBox
                        playlistName.text = playlist.playlistName
                        if (playlist.playlistName == currPlaylistName) {
                            playlistName.isChecked = true
                        }
                        playlistName.setOnClickListener {
                            val checked = playlistName.isChecked
                            if (checked) {
                                sharedViewModel.addVideoToPlaylist(playlist.playlistName, talkItem)
                                sharedViewModel.setItemRemoved()
                                Toast.makeText(requireContext(),
                                    getString(R.string.added_to_playlist, playlist.playlistName),
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                sharedViewModel.removeItemFromPlaylist(playlist.playlistName, talkItem)
                                currPlaylistName = ""
                                sharedViewModel.setItemRemoved()
                                Toast.makeText(requireContext(), "Item was Removed",
                                    Toast.LENGTH_SHORT).show()
                                playlistName.isChecked = false
                            }
                        }
                        checkGroup!!.addView(playlistName)
                    }
                }
                playlistHintVisibility(checkGroup!!)
            }
            checkGroup!!.invalidate()
        })

        sharedViewModel.playlistData.observe(viewLifecycleOwner, Observer {
            if (currPlaylistName.isNotBlank()) {
                for (index in 1 until checkGroup!!.childCount) {
                    val checkBox: CheckBox = checkGroup!!.getChildAt(index) as CheckBox
                    if (checkBox.text.toString() == currPlaylistName) {
                        checkBox.isChecked = true
                        break
                    }
                }
            }
        })
    }


    override fun onStart() {
        super.onStart()
        binding.createNew.setOnClickListener {
            // Closing the Bottom Sheet fragment before showing the Alert Dialog
            dismiss()
            val builder = MaterialAlertDialogBuilder(requireContext())
                .setTitle("\n")

            // Creating the Edit Text view and set it to the Alert Dialog
            val input = EditText(requireContext())
            input.hint = "Enter playlist name"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                // Get input text from the Edittext
                newPlaylistName = input.text.toString()

                if (newPlaylistName.isNotBlank()) {
                    currPlaylistName = newPlaylistName
                    sharedViewModel.createNewPlaylist(newPlaylistName, talkItem)
                }
            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

        binding.doneBtn.setOnClickListener {
            dismiss()
        }
    }


    private fun checkIfPlaylistExist(playlistName: String, checkGroup: ViewGroup): Boolean {
        for (index in 1 until checkGroup.childCount) {
            val checkBox: CheckBox = checkGroup.getChildAt(index) as CheckBox
            if (checkBox.text == playlistName) {
                return true
            }
        }
        return false
    }


    private fun playlistHintVisibility(checkGroup: ViewGroup) {
        if (checkGroup.childCount == 0) {
            binding.playlistLayoutHint.visibility = View.VISIBLE
        } else {
            binding.playlistLayoutHint.visibility = View.GONE
        }
    }
}