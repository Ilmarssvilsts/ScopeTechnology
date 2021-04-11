package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class VehicleLocationList {
    @SerializedName("data")
    var data: List<VehicleLocation>? = null
}