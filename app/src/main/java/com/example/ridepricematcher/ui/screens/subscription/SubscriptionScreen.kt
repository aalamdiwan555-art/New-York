package com.example.ridepricematcher.ui.screens.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ridepricematcher.domain.model.EntitlementType
import com.example.ridepricematcher.ui.viewmodel.SubscriptionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    viewModel: SubscriptionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    val entitlement by viewModel.entitlement.collectAsStateWithLifecycle()
    val adProgress by viewModel.adProgress.collectAsStateWithLifecycle()
    val adState by viewModel.adState.collectAsStateWithLifecycle()

    LaunchedEffect(adState) {
        if (adState is SubscriptionViewModel.AdState.Rewarded) {
            kotlinx.coroutines.delay(2000)
            viewModel.resetAdState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription & Rewards") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current Plan Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Current Plan", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    val planText = when {
                        entitlement?.lifetime == true -> "Lifetime"
                        entitlement?.adFree == true -> "Ad-Free"
                        entitlement?.type == EntitlementType.PREMIUM -> "Premium"
                        else -> "Free"
                    }
                    Text(planText, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    if (entitlement?.isActive() == true) {
                        Text("Active", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // Reward Progress Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Rewarded Ads Progress", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { adProgress / 20f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$adProgress / 20 ads = 1 day subscription", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Watch Ad Button
            Button(
                onClick = {
                    viewModel.onAdRewarded("unity", "demo_reward_id_${System.currentTimeMillis()}")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = adState !is SubscriptionViewModel.AdState.Processing
            ) {
                when (adState) {
                    is SubscriptionViewModel.AdState.Processing ->
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    is SubscriptionViewModel.AdState.Rewarded ->
                        Text("Rewarded! +1")
                    else -> Text("Watch Rewarded Ad")
                }
            }

            if (adState is SubscriptionViewModel.AdState.Error) {
                Text(
                    text = (adState as SubscriptionViewModel.AdState.Error).error.userMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Info
            Text(
                text = "Watch 20 rewarded ads to earn 1 day of premium access. Server verifies all rewards.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
