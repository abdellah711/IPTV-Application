package com.app.tvapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.data.entities.Language
import com.app.tvapp.data.entities.Suggestion

@Database(entities = [DBChannel::class, Suggestion::class,Language::class],version = 1)
abstract class ChannelsDatabase: RoomDatabase() {

    abstract val channelDao:ChannelDao

}