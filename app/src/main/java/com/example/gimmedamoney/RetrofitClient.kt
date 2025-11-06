package com.example.gimmedamoney

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java


class RetrofitClient {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GimmeDaMoneyService by lazy {
        retrofit.create(GimmeDaMoneyService::class.java)
    }
}


