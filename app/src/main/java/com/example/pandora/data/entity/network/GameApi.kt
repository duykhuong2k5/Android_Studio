package com.example.pandora.data.entity.network

import com.example.pandora.data.entity.AiQuestionResponse
import com.example.pandora.data.entity.FinishDayRequest
import com.example.pandora.data.entity.StartDayRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GameApi {

    @POST("api/v1/day/start")
    suspend fun startDay(
        @Body req: StartDayRequest
    ): ApiResponse<DaySessionResponse>

    @POST("api/v1/day/{sessionId}/finish")
    suspend fun finishDay(
        @Path("sessionId") sessionId: Long,
        @Query("userId") userId: Long,
        @Body req: FinishDayRequest
    ): ApiResponse<DaySessionResponse>

    @GET("api/v1/ai/question")
    suspend fun getQuestion(
        @Query("jobType") jobType: String,
        @Query("stepType") stepType: String,
        @Query("level") level: Int
    ): ApiResponse<AiQuestionResponse>
}
