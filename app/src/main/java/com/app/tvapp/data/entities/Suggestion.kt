package com.app.tvapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Suggestion(
    @PrimaryKey(autoGenerate = false)
    val suggestion: String,
    val time: Long
)
