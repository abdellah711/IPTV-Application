package com.app.tvapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Language(
    @PrimaryKey(autoGenerate = false)
    val code: String,
    val name: String
)