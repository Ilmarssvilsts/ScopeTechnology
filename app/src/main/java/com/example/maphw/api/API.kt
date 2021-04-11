package com.example.maphw.api

import com.example.maphw.BuildConfig.BASE_URL
import com.example.maphw.BuildConfig.ROUTE_URL
import com.example.maphw.api.services.UsersService
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object API {

    private val client = OkHttpClient
            .Builder()
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UsersService::class.java)

    fun buildApi(): UsersService {
        return retrofit
    }

    private val retrofitRoute = Retrofit.Builder()
        .baseUrl(ROUTE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(UsersService::class.java)

    fun buildRouteApi(): UsersService {
        return retrofitRoute
    }
}