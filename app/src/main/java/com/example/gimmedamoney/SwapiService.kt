package com.example.gimmedamoney;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SwapiService {
    @GET("people/{id}")
    suspend fun getPerson(@Path("id") personId: Int): MemberViewModel.Member

    @GET("people/")
    suspend fun getPeople():List<MemberViewModel.Member>
}
