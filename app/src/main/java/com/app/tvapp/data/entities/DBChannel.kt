package com.app.tvapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel")
data class DBChannel(
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null,
    val category: String,
    val logo: String,
    val name: String,
    val url: String,
    val isFav: Boolean = false
//    val countries: List<Country>,
//    val tvg: Tvg,
//    val languages: List<Language>
)

