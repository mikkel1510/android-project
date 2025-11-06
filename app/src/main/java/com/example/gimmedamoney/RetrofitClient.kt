package com.example.gimmedamoney

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.getValue
import kotlin.jvm.java


class RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://swapi.dev/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SwapiService by lazy {
        retrofit.create(SwapiService::class.java)
    }
}


