package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class Owner {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("surname")
    var surname: String? = null
    @SerializedName("foto")
    var photo: String? = null
}