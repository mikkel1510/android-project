package com.example.gimmedamoney

data class GroupSummary(
    val id: String,
    val name: String,
    val totalDkk: Double = 0.0,
    val youOweDkk: Double = 0.0,
    val youAreOwedDkk: Double = 0.0
)
