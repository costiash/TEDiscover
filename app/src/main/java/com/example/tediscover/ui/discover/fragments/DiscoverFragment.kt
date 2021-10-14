package com.example.tediscover.ui.discover.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.FragmentDiscoverBinding
import com.example.tediscover.ui.discover.viewmodel.DiscoverViewModel
import com.example.tediscover.ui.models.SearchQuery
import com.example.tediscover.utils.hideKeyboard
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton


class DiscoverFragment : Fragment() {

    private lateinit var binding: FragmentDiscoverBinding
    private val viewModel: DiscoverViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as TedActivity).findViewById<FloatingActionButton>(R.id.my_fab).visibility = View.INVISIBLE
        binding = FragmentDiscoverBinding.inflate(inflater)
        // Setting onTouch Listener on the primary linear layout so it will be possible to close
        // the keyboard by pressing anywhere on the screen
        binding.topLayout.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.textInputEditText.clearFocus()
            binding.textField.clearFocus()
            true
        }
        viewModel.resetDataItems()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chipGroup = binding.chipGroupFilters
        setTopicChipGroupListener(binding.chipGroupTopics)

        // Get the input from the user and updates the viewModel
        binding.textField.editText!!.doOnTextChanged { inputText, _, _, _ ->
            binding.textField.editText?.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    if (inputText.toString().isNotBlank()) {
                        if (viewModel.userInput.value!!.toString().isNotBlank())
                            removeInputChipView(chipGroup)
                        viewModel.setUserInput(inputText.toString())
                    }
                    binding.textField.editText?.text = null
                    binding.textInputEditText.clearFocus()
                    binding.textField.clearFocus()
                }
                true
            }
        }

        binding.chipGroupDuration.setOnCheckedChangeListener { group, checkedId ->
            val oldDuration = viewModel.duration.value
            if (oldDuration!!.isNotEmpty()){
                val filterIds = chipGroup.checkedChipIds
                for (filter in filterIds) {
                    val chip: Chip = chipGroup.findViewById(filter) as Chip
                    if (chip.text.toString() == oldDuration) {
                        viewModel.setDuration("")
                        chipGroup.removeView(chip)
                        break
                    }
                }
            }
            filtersHintVisibility(chipGroup.checkedChipIds)
            // The same checked chip
            if (checkedId == -1) {
                return@setOnCheckedChangeListener
            } else {
                val chip: Chip = group.findViewById(checkedId) as Chip
                viewModel.setDuration(chip.text!!.toString())
            }
        }

        binding.chipGroupSortBy.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) {
                return@setOnCheckedChangeListener
            } else {
                val chip: Chip = group.findViewById(checkedId) as Chip
                viewModel.setSortBy(chip.text!!.toString())
            }
        }
        binding.chipRelevance.isChecked = true // Default choice for the sortBy type.

        // Observe the user's input LiveData, passing in the LifecycleOwner and the observer.
        viewModel.userInput.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                val filterChip = layoutInflater.inflate(R.layout.chip_layout, chipGroup, false) as Chip
                filterChip.text = it
                filterChip.setOnCloseIconClickListener {
//                    viewModel.setUserInput("")
                    chipGroup.removeView(filterChip)
                    filtersHintVisibility(chipGroup.checkedChipIds)
                }
                chipGroup.addView(filterChip)
            }
            filtersHintVisibility(chipGroup.checkedChipIds)
            chipGroup.invalidate();
        })

        // Observe changes in the LiveData of the topics that the user can choose to add to the
        // Filters List.
        viewModel.topics.observe(viewLifecycleOwner, Observer {
            if (viewModel.topics.value!!.isNotEmpty()) {
                for (topic in viewModel.topics.value!!) {
                    if (checkIfTopicInFilters(topic, chipGroup)) {
                        val filterChip = layoutInflater.inflate(R.layout.chip_layout, chipGroup,
                            false) as Chip
                        filterChip.text = topic
                        filterChip.setOnCloseIconClickListener {
                            uncheckTopicChip(filterChip.text.toString())
                            chipGroup.removeView(filterChip)
                            filtersHintVisibility(chipGroup.checkedChipIds)
                        }
                        chipGroup.addView(filterChip)
                    }
                }
            }
            filtersHintVisibility(chipGroup.checkedChipIds)
            chipGroup.invalidate()
        })

        // Observe the Duration filter that was picked by the user.
        viewModel.duration.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                val filterChip = layoutInflater.inflate(R.layout.chip_layout, chipGroup,
                    false) as Chip
                filterChip.text = it
                filterChip.setOnCloseIconClickListener {
                    uncheckDurationChip()
                    chipGroup.removeView(filterChip)
                    filtersHintVisibility(chipGroup.checkedChipIds)
                }
                chipGroup.addView(filterChip)
            }
            filtersHintVisibility(chipGroup.checkedChipIds)
            chipGroup.invalidate()
        })

        binding.clearAll.setOnClickListener {
            clearAllFilters(chipGroup)
        }

        binding.submit.setOnClickListener {
            if (viewModel.sortBy.value!!.isEmpty())
                viewModel.setSortBy("Relevance")
            val searchQuery = SearchQuery(viewModel.duration.value.toString(), viewModel.topics.value!!,
                viewModel.sortBy.value.toString(), viewModel.userInput.value.toString())
            val action = DiscoverFragmentDirections
                .actionNavigationDiscoverFragmentToDiscoverFeedFragment(searchQuery)
            findNavController().navigate(action)
        }
    }


    private fun removeInputChipView(chipGroup: ChipGroup) {
        val oldInputText = viewModel.userInput.value!!
        for (index in 0 until chipGroup.childCount) {
            val chip: Chip = chipGroup.getChildAt(index) as Chip
            if (chip.text.toString() == oldInputText) {
                chipGroup.removeView(chip)
                break
            }
        }

    }


    private fun setTopicChipGroupListener(group: ChipGroup) {
        for (index in 0 until group.childCount) {
            val chip: Chip = group.getChildAt(index) as Chip

            // Set the chip checked change listener
            chip.setOnCheckedChangeListener{view, isChecked ->
                if (isChecked){
                    viewModel.addTopic(view.text.toString())
                }else{
                    removeTopicFilterView(view.text.toString())
                    viewModel.removeTopic(view.text.toString())
                }
            }
        }
    }


    private fun removeTopicFilterView(topic: String) {
        val filtersIds = binding.chipGroupFilters.checkedChipIds
        if (filtersIds.isNotEmpty()) {
            for (filterId in filtersIds) {
                val chip: Chip = binding.chipGroupFilters.findViewById(filterId) as Chip
                if (chip.text.toString() == topic) {
                    binding.chipGroupFilters.removeView(chip)
                    break
                }
            }
        }
    }


    private fun filtersHintVisibility(chipsIdsList: List<Int?>) {
        if (chipsIdsList.isNotEmpty()) {
            binding.filtersLayoutHint.visibility = View.GONE
        } else {
            binding.filtersLayoutHint.visibility = View.VISIBLE
        }
    }


    private fun checkIfTopicInFilters(topic: String, chipsGroup: ChipGroup): Boolean {
        for (index in 0 until chipsGroup.childCount) {
            val chip: Chip = chipsGroup.getChildAt(index) as Chip
            if (chip.text.toString() == topic) {
                return false
            }
        }
        return true
    }


    private fun uncheckTopicChip(topicTile: String) {
        val topicGroupIds = binding.chipGroupTopics.checkedChipIds
        if (topicGroupIds.isNotEmpty()) {
            for (checkedId in topicGroupIds) {
                val chip: Chip = binding.chipGroupTopics.findViewById(checkedId) as Chip
                if (chip.text.toString() == topicTile) {
                    chip.isChecked = false
                    break
                }
            }
        }
    }


    private fun uncheckDurationChip() {
        val checkedChipId = binding.chipGroupDuration.checkedChipId
        if (checkedChipId != -1) {
            val chip: Chip = binding.chipGroupDuration.findViewById(checkedChipId)
            chip.isChecked = false
        }
    }


    private fun clearAllFilters(chipGroup: ChipGroup) {
        val topicsIds = binding.chipGroupTopics.checkedChipIds
        if (topicsIds.isNotEmpty()) {
            for (topicId in topicsIds) {
                val chip: Chip = binding.chipGroupTopics.findViewById(topicId) as Chip
                chip.isChecked = false
            }
        }
        val viewModelDuration = viewModel.duration.value
        if (!viewModelDuration.isNullOrBlank())
            uncheckDurationChip()
        viewModel.resetDataItems()
        if (chipGroup.checkedChipIds.isNotEmpty()) {
            chipGroup.removeAllViews()
            binding.filtersLayoutHint.visibility = View.VISIBLE
        }
        binding.chipRelevance.isChecked = true
        viewModel.setSortBy("Relevance")
    }
}