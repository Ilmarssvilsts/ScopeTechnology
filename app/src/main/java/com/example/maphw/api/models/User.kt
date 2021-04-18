package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("userid")
    var userid: Int? = null

    @SerializedName("owner")
    var owner: Owner? = null

    @SerializedName("vehicles")
    var vehicles: List<Vehicle>? = null
}