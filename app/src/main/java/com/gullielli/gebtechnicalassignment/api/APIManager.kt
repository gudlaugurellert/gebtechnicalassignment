package com.gullielli.gebtechnicalassignment.api

import com.gullielli.gebtechnicalassignment.util.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIManager {

    private val retrofit by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: API by lazy {

        retrofit.create(API::class.java)
    }
}