package com.example.gimmedamoney.data.remote

import com.example.gimmedamoney.core.GimmeDaMoneyService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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