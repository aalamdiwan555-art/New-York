package com.example.ridepricematcher.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ridepricematcher.domain.model.UserProfile
import com.example.ridepricematcher.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val actionResult by viewModel.actionResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = users.filter {
        it.email.contains(searchQuery, ignoreCase = true) ||
        it.displayName.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(actionResult) {
        actionResult?.let {
            val message = when (it) {
                is AdminViewModel.ActionResult.Success -> "Action completed successfully"
                is AdminViewModel.ActionResult.Error -> it.error.userMessage
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search users") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("${filteredUsers.size} users found", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredUsers) { user ->
                        UserAdminCard(user = user, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun UserAdminCard(user: UserProfile, viewModel: AdminViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.displayName.ifBlank { "No name" }, style = MaterialTheme.typography.titleMedium)
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (user.blocked) {
                        Text("BLOCKED", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (user.blocked) {
                        OutlinedButton(onClick = { viewModel.unblockUser(user.id) }) {
                            Text("Unblock")
                        }
                    } else {
                        OutlinedButton(
                            onClick = { viewModel.blockUser(user.id) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Block")
                        }
                    }
                    OutlinedButton(onClick = { viewModel.grantAdFree(user.id) }) {
                        Text("Ad-Free")
                    }
                    OutlinedButton(onClick = { viewModel.grantLifetime(user.id) }) {
                        Text("Lifetime")
                    }
                }
            }
        }
    }
}
