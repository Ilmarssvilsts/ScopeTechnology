package com.example.maphw.api.services

import com.example.maphw.api.models.Route
import com.example.maphw.api.models.UserList
import com.example.maphw.api.models.VehicleLocationList
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersService {
    @GET("/api/?op=list")
    fun getUserList(): Observable<UserList>

    @GET("/api/?op=getlocations")
    fun getCurrentPosition(@Query("userid") identifier: String?): Observable<VehicleLocationList>

    @GET("/maps/api/directions/json?origin={longLat}&sensor=false")
    fun getRoute(@Query("destination") longLatDestination: String?, @Query("key") apiKey: String?, @Path("longLat") longLatStart: String?): Observable<Route>
}