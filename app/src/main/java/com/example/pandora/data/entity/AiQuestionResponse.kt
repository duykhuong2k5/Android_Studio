package com.example.pandora.data.entity

data class AiQuestionResponse(
    val templateId: Long,
    val jobType: String,
    val stepType: String,
    val level: Int,
    val optionType: String,
    val question: String,
    val options: List<String>
)
