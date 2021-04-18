package com.example.maphw

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.maphw.data.Vehicle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso

class InfoWindow(context: Context, vehicles: MutableList<Vehicle>) : GoogleMap.InfoWindowAdapter {

    var mContext = context
    var vehicles = vehicles
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.info_window, null)

    private fun renderText(marker: Marker, view: View){

        val vehicleName = view.findViewById<TextView>(R.id.vehicleName)
        val vehicleAddress = view.findViewById<TextView>(R.id.vehicleAddress)
        val vehicleColor = view.findViewById<ImageView>(R.id.vehicleColor)
        val vehicleImg = view.findViewById<ImageView>(R.id.vehicleImg)


        for(i in vehicles){
            if(i.vehicleId == marker.tag){
                vehicleName.text = i.make + " " + i.model
                val drawable = vehicleColor.background as GradientDrawable
                drawable.setColor(Color.parseColor(i.color))

                Picasso.get().load(i.photo?.replace("http:", "https:"))
                        .into(vehicleImg)
                vehicleAddress.text = marker.snippet
            }
        }

    }

    override fun getInfoContents(marker: Marker): View {
        renderText(marker, mWindow)
        return mWindow
    }

    override fun getInfoWindow(marker: Marker): View? {
        renderText(marker, mWindow)
        return mWindow
    }
}
