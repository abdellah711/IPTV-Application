package com.app.tvapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tvapp.data.database.ChannelDao
import com.app.tvapp.data.entities.DBChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val channelDao: ChannelDao
): ViewModel() {

    var isFav = false
    var channel: DBChannel? = null
        set(value){
            isFav = value?.isFav?:false
            field = value
        }

    fun saveFav(fav: Boolean){
        isFav = fav
        channel?.isFav = fav
        viewModelScope.launch(Dispatchers.IO) {
            channel?.let {
                channelDao.updateChannel(it)
            }

        }
    }

}