package com.app.tvapp.data.entities

import androidx.room.*

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

@Entity(primaryKeys = ["id","code"])
data class ChannelLangCrossRef(
    var id: Long,
    var code: String
)

data class ChannelWithLangs(
    @Embedded var channel: DBChannel,
    @Relation(
        parentColumn = "id",
        entityColumn = "code",
        associateBy = Junction(ChannelLangCrossRef::class)
    )
    val langs: List<Language>
)

