package com.app.tvapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Suggestion(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val suggestion: String
)
