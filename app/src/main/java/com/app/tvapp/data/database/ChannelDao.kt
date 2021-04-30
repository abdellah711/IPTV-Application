package com.app.tvapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.data.entities.Suggestion

@Dao
interface ChannelDao {

    @Query("SELECT COUNT(*) FROM channel")
    suspend fun getCount(): Int

    @Query("SELECT * FROM channel WHERE logo != \"\" LIMIT :lim")
    fun getAllChannels(lim: Int): LiveData<List<DBChannel>>

    @Query("SELECT * FROM channel WHERE instr(lower(name),lower(:text))")
    fun searchForChannel(text: String): List<DBChannel>

    @Query("SELECT * FROM channel WHERE isFav")
    fun getFavorites(): LiveData<List<DBChannel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(vararg channel:DBChannel)

    @Query("SELECT * FROM suggestion ORDER BY time DESC LIMIT 10")
    fun getSuggestions(): LiveData<List<Suggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(sug: Suggestion)

    @Delete
    suspend fun deleteSuggestion(sug: Suggestion)

}