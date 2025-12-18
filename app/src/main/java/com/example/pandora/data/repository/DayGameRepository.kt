package com.example.pandora.data.repository

import com.example.pandora.data.entity.FinishDayRequest
import com.example.pandora.data.entity.StartDayRequest
import com.example.pandora.data.entity.network.GameApi

class DayGameRepository(
    private val api: GameApi
) {

    suspend fun startDay(userId: Long, jobType: String) =
        api.startDay(StartDayRequest(userId, jobType))

    suspend fun finishDay(
        sessionId: Long,
        userId: Long,
        score: Int,
        correct: Int,
        wrong: Int,
        stars: Int
    ) =
        api.finishDay(
            sessionId,
            userId,
            FinishDayRequest(score, correct, wrong, stars)
        )

    suspend fun loadQuestion(
        jobType: String,
        step: String,
        level: Int
    ) =
        api.getQuestion(jobType, step, level)
}
