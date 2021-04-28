package com.app.tvapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.tvapp.data.entities.DBChannel

@Dao
interface ChannelDao {

    @Query("SELECT COUNT(*) FROM channel")
    suspend fun getCount(): Int

    @Query("SELECT * FROM channel WHERE logo != \"\" LIMIT :lim")
    fun getAllChannels(lim: Int): LiveData<List<DBChannel>>

    @Query("SELECT * FROM channel WHERE instr(name,:text)")
    fun searchForChannel(text: String): LiveData<List<DBChannel>>

    @Query("SELECT * FROM channel WHERE isFav")
    fun getFavorites(): LiveData<List<DBChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(vararg channel:DBChannel)

}