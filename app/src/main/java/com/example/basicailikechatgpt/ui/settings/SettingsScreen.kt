package com.example.basicailikechatgpt.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var darkMode by remember { mutableStateOf(true) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            ListItem(
                headlineContent = { Text("Dark Mode") },
                trailingContent = {
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { darkMode = it }
                    )
                }
            )
            
            ListItem(
                headlineContent = { Text("Notifications") },
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            ListItem(
                headlineContent = { Text("Version") },
                supportingContent = { Text("1.0.0") }
            )
            
            ListItem(
                headlineContent = { Text("Developer") },
                supportingContent = { Text("AI Assistant Team") }
            )
        }
    }
}