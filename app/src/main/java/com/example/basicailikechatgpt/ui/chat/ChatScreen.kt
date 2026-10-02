package com.example.basicailikechatgpt.ui.chat

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.basicailikechatgpt.data.model.Role
import com.example.basicailikechatgpt.data.repository.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
                if (isLoading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(8.dp))
                    }
                }
            }

            Surface(tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask about code, debugging, or logic...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == Role.USER.value
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    AssistantMessageContent(message.content)
                } else {
                    Text(text = message.content, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun AssistantMessageContent(content: String) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val parts = content.split("```")
    
    Column {
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) { // This is a code block
                CodeBlock(code = part.trim())
            } else if (part.isNotBlank()) {
                Text(text = part.trim(), style = MaterialTheme.typography.bodyLarge)
            }
        }
        
        Row(modifier = Modifier.align(Alignment.End)) {
            IconButton(onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }) {
                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { clipboardManager.setText(AnnotatedString(content)) }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy All", modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun CodeBlock(code: String) {
    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
    ) {
        SelectionContainer {
            Text(
                text = code,
                color = Color(0xFFD4D4D4),
                modifier = Modifier.padding(12.dp),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        }
    }
}