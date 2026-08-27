package com.example.ridepricematcher.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ridepricematcher.domain.model.EntitlementType
import com.example.ridepricematcher.domain.model.LanguageConfig
import com.example.ridepricematcher.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val entitlement by viewModel.entitlement.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val adProgress by viewModel.adProgress.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val languages by viewModel.languages.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ride Price Matcher") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    if (profile?.isAdmin == true) {
                        IconButton(onClick = onNavigateToAdmin) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    WelcomeCard(name = profile?.displayName ?: "Rider")
                }
                item {
                    EntitlementCard(entitlement = entitlement)
                }
                item {
                    AdProgressCard(progress = adProgress, onClick = onNavigateToSubscription)
                }
                item {
                    MonitoringStatusCard(
                        enabled = preferences?.matchingEnabled ?: false,
                        languages = languages.filter { it.locale in (preferences?.selectedLanguages ?: emptyList()) },
                        minPrice = preferences?.minimumPrice,
                        maxPrice = preferences?.maximumPrice
                    )
                }
                item {
                    QuickActionsCard(
                        onSettings = onNavigateToSettings,
                        onSubscription = onNavigateToSubscription,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeCard(name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun EntitlementCard(entitlement: com.example.ridepricematcher.domain.model.Entitlement?) {
    val type = entitlement?.type ?: EntitlementType.FREE
    val isActive = entitlement?.isActive() ?: false
    val title = when {
        entitlement?.lifetime == true -> "Lifetime Access"
        entitlement?.adFree == true -> "Ad-Free"
        type == EntitlementType.PREMIUM -> "Premium"
        type == EntitlementType.AD_REWARDED -> "Ad Rewarded"
        else -> "Free Plan"
    }
    val color = when {
        entitlement?.lifetime == true -> MaterialTheme.colorScheme.tertiary
        isActive -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive) Icons.Default.Verified else Icons.Default.Lock,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = if (isActive) "Active" else "Inactive",
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
            }
        }
    }
}

@Composable
fun AdProgressCard(progress: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reward Progress", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress / 20f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$progress / 20 ads watched",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MonitoringStatusCard(
    enabled: Boolean,
    languages: List<LanguageConfig>,
    minPrice: Double?,
    maxPrice: Double?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (enabled) Icons.Default.Radar else Icons.Default.Radar,
                    contentDescription = null,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Monitoring", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (enabled) "Active — watching for matches" else "Paused",
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            if (languages.isNotEmpty()) {
                Text(
                    text = "Languages: ${languages.joinToString { it.displayName }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (minPrice != null || maxPrice != null) {
                Text(
                    text = "Range: ₹${minPrice ?: "Any"} - ₹${maxPrice ?: "Any"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QuickActionsCard(
    onSettings: () -> Unit,
    onSubscription: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            ListItem(
                headlineContent = { Text("Settings") },
                leadingContent = { Icon(Icons.Default.Settings, null) },
                modifier = Modifier.clickable { onSettings() }
            )
            ListItem(
                headlineContent = { Text("Subscription & Rewards") },
                leadingContent = { Icon(Icons.Default.CardMembership, null) },
                modifier = Modifier.clickable { onSubscription() }
            )
            ListItem(
                headlineContent = { Text("Logout") },
                leadingContent = { Icon(Icons.Default.Logout, null) },
                modifier = Modifier.clickable { onLogout() }
            )
        }
    }
}
