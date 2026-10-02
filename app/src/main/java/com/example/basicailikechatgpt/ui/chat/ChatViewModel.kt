package com.example.basicailikechatgpt.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.basicailikechatgpt.data.model.Message
import com.example.basicailikechatgpt.data.model.Role
import com.example.basicailikechatgpt.data.repository.ChatMessage
import com.example.basicailikechatgpt.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadChatHistory()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            repository.getChatHistory().collect {
                _messages.value = it
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val userMsg = ChatMessage(role = Role.USER.value, content = content)
            repository.saveMessage(userMsg)

            _isLoading.value = true
            
            val apiMessages = _messages.value.map { Message(role = it.role, content = it.content) }
            val result = repository.getAiResponse(apiMessages + Message(role = Role.USER.value, content = content))
            
            _isLoading.value = false
            
            result.onSuccess { aiMsg ->
                repository.saveMessage(ChatMessage(role = aiMsg.role, content = aiMsg.content))
            }.onFailure {
                repository.saveMessage(ChatMessage(role = Role.ASSISTANT.value, content = "Error: ${it.message}"))
            }
        }
    }
}