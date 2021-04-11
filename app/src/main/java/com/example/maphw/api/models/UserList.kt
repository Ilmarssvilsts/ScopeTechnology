package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class UserList {
    @SerializedName("data")
    var data: List<User>? = null
}