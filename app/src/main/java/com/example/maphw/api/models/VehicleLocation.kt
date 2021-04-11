package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class VehicleLocation {
    @SerializedName("vehicleid")
    var vehicleId: Int? = null
    @SerializedName("lat")
    var lat: String? = null
    @SerializedName("lon")
    var lon: String? = null
}