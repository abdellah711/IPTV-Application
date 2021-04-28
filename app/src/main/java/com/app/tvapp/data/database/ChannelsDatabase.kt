package com.app.tvapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.tvapp.data.entities.DBChannel

@Database(entities = [DBChannel::class],version = 1)
abstract class ChannelsDatabase: RoomDatabase() {

    abstract val channelDao:ChannelDao
}