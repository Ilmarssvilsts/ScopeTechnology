package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class Vehicle {
    @SerializedName("vehicleid")
    var vehicleId: Int? = null
    @SerializedName("make")
    var make: String? = null
    @SerializedName("model")
    var model: String? = null
    @SerializedName("year")
    var year: String? = null
    @SerializedName("color")
    var color: String? = null
    @SerializedName("vin")
    var vin: String? = null
    @SerializedName("foto")
    var photo: String? = null
}