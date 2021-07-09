package com.app.tvapp.data.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "channel")
data class DBChannel(
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null,
    val category: String,
    val logo: String,
    val name: String,
    val url: String,
    var isFav: Boolean = false
//    val countries: List<Country>,
//    val tvg: Tvg,
//    val languages: List<Language>
): Parcelable

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

