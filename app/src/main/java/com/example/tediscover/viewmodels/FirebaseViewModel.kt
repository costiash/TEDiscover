package com.example.tediscover.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tediscover.firebase.UserData
import com.example.tediscover.ui.models.PlaylistData
import com.example.tediscover.ui.models.TalkItem
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class FirebaseViewModel : ViewModel() {

    private var _firebaseDbInstance: FirebaseFirestore = Firebase.firestore
    private var _currentUser: FirebaseUser = Firebase.auth.currentUser!!
    private var _currentUserId: String? = _currentUser.uid

    private val _firebaseUser = MutableLiveData<UserData>()
    val firebaseUser: LiveData<UserData> = _firebaseUser

    private val _likeDocs = MutableLiveData<List<TalkItem>>()
    val likeDocs: LiveData<List<TalkItem>> = _likeDocs

    private val _historyDocs = MutableLiveData<List<TalkItem>>()
    val historyDocs: LiveData<List<TalkItem>> = _historyDocs

    private val _playlists = MutableLiveData<List<PlaylistData>>()
    val playlists: LiveData<List<PlaylistData>> = _playlists

    private val _playlistData = MutableLiveData<Map<String, List<TalkItem>>>()
    val playlistData: LiveData<Map<String, List<TalkItem>>> = _playlistData

    private val _newPlaylistName = MutableLiveData<String>()
    val newPlaylistName: LiveData<String> = _newPlaylistName

    private val _isItemRemovedFromPlaylist = MutableLiveData<Boolean>()
    val isItemRemovedFromPlaylist: LiveData<Boolean> = _isItemRemovedFromPlaylist

    private val _currentPlaylist = MutableLiveData<PlaylistData>()
    val currentPlaylist: LiveData<PlaylistData> = _currentPlaylist

    private var inPlaylist = ""


    init {
        _newPlaylistName.value = ""
        _isItemRemovedFromPlaylist.value = false
        getFirebaseUser()
        getProfileDocs()
    }


    private fun getFirebaseUser() {
        val docRef = _firebaseDbInstance.collection("users").document(_currentUserId!!)

        viewModelScope.launch(IO) {
            docRef.get().addOnSuccessListener {
                _firebaseUser.value = it.toObject(UserData::class.java)
            }
        }
    }


    private fun getProfileDocs() {
        viewModelScope.launch {
            getLikeDocs()
            getHistoryDocs()
            getPlaylists()
        }
    }


    private fun getLikeDocs() {
        val likeDocsList = mutableListOf<TalkItem>()
        val likeDocRef = _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("likedVideos")

        viewModelScope.launch(IO) {
            likeDocRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val videoItem = document.toObject(TalkItem::class.java)
                    likeDocsList.add(videoItem)
                }
                _likeDocs.value = likeDocsList
            }
        }
    }


    private fun getHistoryDocs() {
        val historyDocList = mutableListOf<TalkItem>()
        val historyDocRef = _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("historyVideos")

        viewModelScope.launch(IO) {
            historyDocRef.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val videoItem = document.toObject(TalkItem::class.java)
                    historyDocList.add(videoItem)
                }
                _historyDocs.value = historyDocList
            }
        }
    }


    private fun getPlaylists() {
        val playlistsList = mutableListOf<PlaylistData>()
        val playlistsDocRef = _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("playlists")

        viewModelScope.launch {
            playlistsDocRef.get().addOnSuccessListener { documents ->
                for(playlistDoc in documents) {
                    val playlistData = playlistDoc.toObject(PlaylistData::class.java)
                    playlistsList.add(playlistData)
                }
                _playlists.value = playlistsList
                updatePlaylistMap()
            }
        }
    }


    private fun updatePlaylistMap() {
        if (_playlists.value != null) {
            val playlistsMap = mutableMapOf<String, List<TalkItem>>()
            for (playlist in _playlists.value!!) {
                val playlistName = playlist.playlistName
                val playlistList = playlist.playlistItems
                playlistsMap[playlistName] = playlistList
            }
            _playlistData.postValue(playlistsMap)
        }
    }


    fun addNewLikeVideo(talkItem: TalkItem) {
        _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("likedVideos").document(talkItem.id)
            .set(talkItem)

        getLikeDocs()
    }


    fun removeLikeVideo(talkItem: TalkItem) {
        _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("likedVideos").document(talkItem.id).delete()

        getLikeDocs()
    }


    fun checkIfLiked(videoId: String): Boolean {
        for (likedVideo in _likeDocs.value!!) {
            if (likedVideo.id == videoId) {
                return true
            }
        }
        return false
    }


    fun addHistoryVideo(talkItem: TalkItem) {
        _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("historyVideos").document(talkItem.id)
            .set(talkItem)

        getHistoryDocs()
    }


    fun removeHistoryVideo(talkItem: TalkItem) {
        _firebaseDbInstance.collection("users").document(_currentUserId!!)
            .collection("historyVideos").document(talkItem.id).delete()

        getHistoryDocs()
    }


    fun createNewPlaylist(playlistName: String, talkItem: TalkItem) {
        if (!checkIfPlaylistExists(playlistName)) {
            val playlistItems = mutableListOf<TalkItem>()
            playlistItems.add(talkItem)
            val videosNum = playlistItems.size
            val playlistData = PlaylistData(playlistName, playlistItems, videosNum)

            _firebaseDbInstance.collection("users").document(_currentUserId!!)
                .collection("playlists").document(playlistName).set(playlistData)

            _newPlaylistName.value = playlistName
            getPlaylists()
        }
    }


    fun addVideoToPlaylist(playlistName: String, talkItem: TalkItem) {
        var playlistDataObject: PlaylistData? = null
        for (playlistData in _playlists.value!!) {
            if (playlistData.playlistName == playlistName) {
                playlistDataObject = playlistData
                break
            }
        }
        if (playlistDataObject != null) {
            if (talkItem !in playlistDataObject.playlistItems) {
                val updatedPlaylist = playlistDataObject.playlistItems
                updatedPlaylist.add(talkItem)
                val updatedSize = updatedPlaylist.size

                _firebaseDbInstance.collection("users").document(_currentUserId!!)
                    .collection("playlists").document(playlistName)
                    .update("playlistItems", updatedPlaylist)

                _firebaseDbInstance.collection("users").document(_currentUserId!!)
                    .collection("playlists").document(playlistName)
                    .update("videosNumber", updatedSize)

                getPlaylists()
            }
        }
    }


    fun removeItemFromPlaylist(playlistName: String, talkItem: TalkItem) {
        var playlistDataObject: PlaylistData? = null
        for (playlistData in _playlists.value!!) {
            if (playlistData.playlistName == playlistName) {
                playlistDataObject = playlistData
                break
            }
        }
        if (playlistDataObject != null) {
            if (talkItem in playlistDataObject.playlistItems) {
                val updatedPlaylist = playlistDataObject.playlistItems
                updatedPlaylist.remove(talkItem)
                val updatedSize = updatedPlaylist.size

                _firebaseDbInstance.collection("users").document(_currentUserId!!)
                    .collection("playlists").document(playlistName)
                    .update("playlistItems", updatedPlaylist)

                _firebaseDbInstance.collection("users").document(_currentUserId!!)
                    .collection("playlists").document(playlistName)
                    .update("videosNumber", updatedSize)

                getPlaylists()
            }
        }
    }


    fun checkIfInPlaylist(talkItem: TalkItem): Boolean {
        for (playlist in _playlists.value!!) {
            if (talkItem in playlist.playlistItems) {
                inPlaylist = playlist.playlistName
                return true
            }
        }
        return false
    }


    fun markPlaylistAsChecked(talkItem: TalkItem): String {
        return inPlaylist
    }


    fun resetNewPlaylistName() {
        _newPlaylistName.value = ""
    }


    fun setItemRemoved() {
        _isItemRemovedFromPlaylist.value = !_isItemRemovedFromPlaylist.value!!
    }


    fun setPlaylist(playlistName: String) {
        _playlists.value?.forEach {
            if (it.playlistName == playlistName) {
                _currentPlaylist.postValue(it)
            }
        }
    }


    fun removeFromList(talkItem: TalkItem) {
        val temp = _currentPlaylist.value!!
        val filtered = temp.playlistItems.filter{ it != talkItem }
        val newData = PlaylistData(temp.playlistName, filtered as MutableList<TalkItem>, filtered.size)
        _currentPlaylist.value = newData
    }


    private fun checkIfPlaylistExists(playlistName: String): Boolean {
        for (playlist in _playlists.value!!) {
            if (playlistName == playlist.playlistName) {
                return true
            }
        }
        return false
    }
}