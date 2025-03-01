package com.khmerpress.core.features.models

import java.io.Serializable

data class Station(
    var id: String,
    var name: String? = null,
    var url: String? = null,
    var category: String? = null,
    var subCategory: String? = null,
    var image: String? = null,
    var type: String? = null,
    var title: String? = null,
    var link: String? = null,
    var external: String? = null,
    var content: String? = null,
    var isMedia: Boolean = false,
    var isAd: Boolean = false,
    var isRec: Boolean = false
) : Serializable {
    fun getLinkOrUrl(): String? =
        if (link.isNullOrBlank() || link.equals("null", ignoreCase = true)) url else link
}