package com.app.tvapp.data.entities


data class Channel(
    val category: String?,
    val countries: List<Country>,
    val languages: List<Language>,
    val logo: String?,
    val name: String,
    val tvg: Tvg,
    val url: String
) {

    fun toDBChannel() = ChannelWithLangs(
        DBChannel(
            category = category ?: "",
            logo = logo ?: "",
            name = name,
            url = url
        ),
        languages
    )

}