package com.app.tvapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.app.tvapp.data.database.ChannelDao
import com.app.tvapp.data.entities.ChannelWithLangs
import com.app.tvapp.data.entities.Suggestion
import com.app.tvapp.data.network.ChannelApi
import com.app.tvapp.others.Constants
import com.app.tvapp.others.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val channelApi: ChannelApi
) : ViewModel() {

    val channels = Pager(PagingConfig(Constants.PAGING_SIZE)) {
        channelDao.getAllChannels()
    }.liveData.cachedIn(viewModelScope)

    val favs = Pager(PagingConfig(Constants.PAGING_SIZE)) {
        channelDao.getFavorites()
    }.liveData

    val suggestions = channelDao.getSuggestions()

    var searchResult: MutableLiveData<Resource<List<ChannelWithLangs>>> =
        MutableLiveData(Resource.Loading())

    var isFetching = MutableLiveData(true)

    var networkError = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {

            if (channelDao.getCount() < 1000) {
                networkError.postValue(!fetchData())
            }
            isFetching.postValue(false)
        }
    }

    private suspend fun fetchData(): Boolean{
        try {
            val response = channelApi.getChannels()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { lst ->

                    val data = lst.map {
                        it.toDBChannel()
                    }.toTypedArray()

                    channelDao.insertChannel(*data)
                }
                return true
            }

        } catch (e: Exception) {
            Log.e("View Model", "fetch err: ${e.message}")

        }
        return false
    }


    fun search(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            channelDao.insertSuggestion(
                Suggestion(text.toLowerCase(Locale.getDefault()), System.currentTimeMillis())
            )
            searchResult.postValue(Resource.Loading())
            searchResult.postValue(Resource.Success(channelDao.searchForChannel(text)))
        }
    }

    fun deleteSuggestion(suggestion: Suggestion) {
        viewModelScope.launch(Dispatchers.IO) {
            channelDao.deleteSuggestion(suggestion)
        }
    }

    fun deleteAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            channelDao.deleteAllChannels()
            channelDao.deleteAllSuggestions()
            fetchData()
        }
    }


}