package com.example.basicailikechatgpt.data.repository

import com.example.basicailikechatgpt.data.model.ChatRequest
import com.example.basicailikechatgpt.data.model.Message
import com.example.basicailikechatgpt.data.remote.OpenAiApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: OpenAiApi,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Replace with your actual OpenAI API Key or fetch from a secure backend
    private val apiKey = "Bearer YOUR_OPENAI_API_KEY"

    suspend fun getAiResponse(messages: List<Message>): Result<Message> {
        return try {
            val systemPrompt = Message(
                role = "system",
                content = "You are a professional AI Assistant specializing in programming. " +
                        "Provide code examples in multiple languages, explain them step by step, " +
                        "and help debug issues. Format code using markdown."
            )
            val fullMessages = listOf(systemPrompt) + messages
            val response = api.getChatCompletion(apiKey, ChatRequest(messages = fullMessages))
            Result.success(response.choices.first().message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getChatHistory(): Flow<List<ChatMessage>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val subscription = firestore.collection("users").document(userId)
            .collection("chats")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { it.toObject(ChatMessage::class.java) } ?: emptyList()
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun saveMessage(message: ChatMessage) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .collection("chats")
            .add(message)
            .await()
    }
}

data class ChatMessage(
    val role: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)