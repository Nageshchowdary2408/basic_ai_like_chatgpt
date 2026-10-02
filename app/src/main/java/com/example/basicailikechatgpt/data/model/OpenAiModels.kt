package com.example.basicailikechatgpt.data.model

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val role: String,
    val content: String
)

enum class Role(val value: String) {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system")
}