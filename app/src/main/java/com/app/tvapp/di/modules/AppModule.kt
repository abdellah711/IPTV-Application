package com.app.tvapp.di.modules

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.app.tvapp.data.database.ChannelsDatabase
import com.app.tvapp.data.network.ChannelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideChannelApi(): ChannelApi = Retrofit.Builder()
        .baseUrl("https://iptv-org.github.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChannelApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ChannelsDatabase::class.java, "tv_db")
            .build()

    @Provides
    @Singleton
    fun provideChannelDao(database: ChannelsDatabase) = database.channelDao

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("app_pref", MODE_PRIVATE)

//    @Provides
//    @Singleton
//    fun provide
}