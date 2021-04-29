package com.app.tvapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tvapp.data.database.ChannelDao
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.data.network.ChannelApi
import com.app.tvapp.others.Resource
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
    var searchResult: MutableLiveData<Resource<List<DBChannel>>> =
        MutableLiveData(Resource.Loading())

    init {
        viewModelScope.launch(Dispatchers.IO) {

            if (channelDao.getCount() == 0) {
                fetchData()
            }

        }
    }

    private suspend fun fetchData() {
        try {
            val response = channelApi.getChannels()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let {
                    it.forEach { channel ->
                        channelDao.insertChannel(channel.toDBChannel())
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("View Model", "fetch err: ${e.message}")
        }

    }


    fun search(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResult.postValue(Resource.Loading())
            searchResult.postValue(Resource.Success(channelDao.searchForChannel(text)))
        }
    }


}