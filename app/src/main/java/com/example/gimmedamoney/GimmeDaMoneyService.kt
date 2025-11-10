package com.example.gimmedamoney;

import retrofit2.http.GET
interface GimmeDaMoneyService {

    data class ApiResponse(
        val id: String,
        val name: String
    )

    /*@GET("people/{id}") //
    suspend fun getPerson(@Path("id") personId: Int): MemberViewModel.Member
    */

    @GET("mikkel1510/android-project/refs/heads/main/data.json")
    suspend fun getUsers(): List<ApiResponse>
}
