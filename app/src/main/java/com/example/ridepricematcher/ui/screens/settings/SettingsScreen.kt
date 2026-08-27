package com.example.ridepricematcher.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ridepricematcher.domain.model.PriceRule
import com.example.ridepricematcher.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit
) {
    val priceRule by viewModel.priceRule.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val saveState by viewModel.saveState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            Text("Price Rules", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = priceRule.minimumFare?.toString() ?: "",
                onValueChange = {
                    viewModel.updatePriceRule(
                        priceRule.copy(minimumFare = it.toDoubleOrNull())
                    )
                },
                label = { Text("Minimum Fare (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("₹") }
            )

            OutlinedTextField(
                value = priceRule.maximumFare?.toString() ?: "",
                onValueChange = {
                    viewModel.updatePriceRule(
                        priceRule.copy(maximumFare = it.toDoubleOrNull())
                    )
                },
                label = { Text("Maximum Fare (₹)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("₹") }
            )

            OutlinedTextField(
                value = priceRule.exactFare?.toString() ?: "",
                onValueChange = {
                    viewModel.updatePriceRule(
                        priceRule.copy(exactFare = it.toDoubleOrNull())
                    )
                },
                label = { Text("Exact Fare (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                prefix = { Text("₹") }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Enable Matching", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = preferences?.matchingEnabled ?: false,
                    onCheckedChange = { viewModel.toggleMatching(it) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.savePreferences() },
                modifier = Modifier.fillMaxWidth()
            ) {
                when (saveState) {
                    is SettingsViewModel.SaveState.Saving ->
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    is SettingsViewModel.SaveState.Saved ->
                        Text("Saved!")
                    else ->
                        Text("Save Settings")
                }
            }
        }
    }
}
