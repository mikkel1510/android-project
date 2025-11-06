package com.example.gimmedamoney;

import retrofit2.http.GET
import retrofit2.http.Path

data class SwapiPerson(
    val name: String,
    val height: String,
    val mass: String,
    val hair_color: String,
    val skin_color: String,
    val eye_color: String,
    val birth_year: String,
    val gender: String,
    val homeworld: String,
    val films: List<String>,
    val species: List<String>,
    val vehicles: List<String>,
    val starships: List<String>,
    val created: String,
    val edited: String,
    val url: String
)

data class SwapiListResponse<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

interface GimmeDaMoneyService {
    @GET("people/{id}")
    suspend fun getPerson(@Path("id") personId: Int): SwapiPerson

    @GET("people")
    suspend fun getPeople(): SwapiListResponse<SwapiPerson>
}
