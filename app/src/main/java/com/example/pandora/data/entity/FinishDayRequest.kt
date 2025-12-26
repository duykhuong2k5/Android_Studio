package com.example.pandora.data.entity

data class FinishDayRequest(
    val score: Int,
    val correct: Int,
    val wrong: Int,
    val stars: Int
)
