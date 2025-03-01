package com.khmerpress.core.features.models

import java.io.Serializable

data class Menu(
    var id: String,
    var name: String,
    var icon: String
) : Serializable
