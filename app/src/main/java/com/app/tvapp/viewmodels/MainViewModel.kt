package com.app.tvapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tvapp.data.database.ChannelDao
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.data.network.ChannelApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val channelApi: ChannelApi
) : ViewModel() {

    val channels = channelDao.getAllChannels(100)
    val favs = channelDao.getFavorites()

    init {
        viewModelScope.launch(Dispatchers.IO) {

            if (channelDao.getCount() == 0) {
                fetchData()
            }

        }
    }

    private suspend fun fetchData() {
        try {
            Log.e("View Model", "fetching...")
            val response = channelApi.getChannels()
            if (response.isSuccessful && response.body() != null) {
                Log.e("View Model", "fetching success")
                response.body()?.let {
                    it.forEach { channel ->
                        channelDao.insertChannel(channel.toDBChannel())
                    }
                }
                Log.e("View Model", "insert success")
            }
        } catch (e: Exception) {
            Log.e("View Model", "fetch err: ${e.message}")
        }

    }


    fun search(text: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = channelDao.searchForChannel(text)
//            _channels.postValue(result)
//        }
    }


}