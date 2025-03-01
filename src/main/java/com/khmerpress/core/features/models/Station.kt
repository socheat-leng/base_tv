package com.khmerpress.core.features.models

import java.io.Serializable

data class Station(
    var id: String,
    var name: String? = "",
    var url: String? = "",
    var category: String? = "",
    var subCategory: String? = "",
    var image: String? = "",
    var type: String? = "",
    var title: String? = "",
    var link: String? = "",
    var external: String? = "",
    var content: String? = "",
    var isMedia: Boolean = false,
    var isAd: Boolean = false,
    var isRec: Boolean = false
) : Serializable {
    fun getLinkOrUrl(): String? =
        if (link.isNullOrBlank() || link.equals("null", ignoreCase = true)) url else link
}