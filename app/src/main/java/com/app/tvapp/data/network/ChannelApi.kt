package com.app.tvapp.data.network

import com.app.tvapp.data.entities.Channel
import retrofit2.Response
import retrofit2.http.GET


interface ChannelApi {

    @GET("iptv/channels.json")
    suspend fun getChannels(): Response<List<Channel>>

}