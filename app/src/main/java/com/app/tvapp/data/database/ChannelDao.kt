package com.app.tvapp.data.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import androidx.room.*
import com.app.tvapp.data.entities.*

@Dao
interface ChannelDao {

    @Query("SELECT COUNT(*) FROM channel")
    suspend fun getCount(): Int

    @Transaction
    @Query("SELECT * FROM channel WHERE logo != \'\'")
    fun getAllChannels(): PagingSource<Int,ChannelWithLangs>

    @Transaction
    @Query("SELECT * FROM channel WHERE instr(lower(name),lower(:text))")
    fun searchForChannel(text: String): List<ChannelWithLangs>

    @Transaction
    @Query("SELECT * FROM channel WHERE isFav")
    fun getFavorites(): PagingSource<Int,ChannelWithLangs>


    suspend fun insertChannel(vararg channel:ChannelWithLangs){
        channel.forEach {
            val id = insertDBChannel(it.channel)
            it.langs.forEach { lng ->
                insertLang(lng)
                insertChannelLangCrossRef(
                    ChannelLangCrossRef(id,lng.code)
                )
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannelLangCrossRef(channelLangCrossRef: ChannelLangCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLang(language: Language)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDBChannel(channel: DBChannel): Long

    @Query("SELECT * FROM suggestion ORDER BY time DESC LIMIT 10")
    fun getSuggestions(): LiveData<List<Suggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(sug: Suggestion)

    @Delete
    suspend fun deleteSuggestion(sug: Suggestion)

}