package com.example.basicailikechatgpt.data.remote

import com.example.basicailikechatgpt.data.model.ChatRequest
import com.example.basicailikechatgpt.data.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApi {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") apiKey: String,
        @Body request: ChatRequest
    ): ChatResponse

    companion object {
        const val BASE_URL = "https://api.openai.com/"
    }
}