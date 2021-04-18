package com.example.maphw.api.models

import com.google.gson.annotations.SerializedName

class Route {
    @SerializedName("routes")
    var vehicles: List<Direction>? = null
}

class Direction {
    @SerializedName("overview_polyline")
    var vehicles: OverviewPolyLine? = null
}

class OverviewPolyLine {
    @SerializedName("points")
    var vehicles: String? = null
}