package com.example.ridepricematcher.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ridepricematcher.ui.viewmodel.OnboardingViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onComplete: () -> Unit
) {
    val completed by viewModel.completed.collectAsStateWithLifecycle()
    val totalPages by viewModel.totalPages.collectAsStateWithLifecycle()
    val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { totalPages })

    LaunchedEffect(completed) {
        if (completed) onComplete()
    }

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            OnboardingPage(page = page)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { viewModel.skipToEnd() },
                enabled = pagerState.currentPage < totalPages - 1
            ) {
                Text("Skip")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(totalPages) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (selected) 10.dp else 6.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .animateContentSize()
                            .then(
                                if (selected) Modifier else Modifier.padding(2.dp)
                            )
                            .drawBehind {
                                drawCircle(
                                    color = if (selected) androidx.compose.ui.graphics.Color(0xFF00D4AA)
                                    else androidx.compose.ui.graphics.Color(0xFF666666)
                                )
                            }
                    )
                }
            }

            Button(onClick = { viewModel.nextPage() }) {
                Text(if (pagerState.currentPage == totalPages - 1) "Get Started" else "Next")
            }
        }
    }
}

@Composable
fun OnboardingPage(page: Int) {
    val pages = listOf(
        Pair("Welcome to Autopilot", "Find the best ride fares automatically across supported apps."),
        Pair("How It Works", "Set your price rules, and we'll watch for matching ride offers in real-time."),
        Pair("Supported Languages", "English, Hindi, Kannada, Telugu, Tamil, Bengali, Marathi, Malayalam, and more."),
        Pair("Price Settings", "Configure minimum, maximum, or exact fare targets that matter to you."),
        Pair("Overlay", "A floating window shows match status without leaving your ride app."),
        Pair("Accessibility", "We use Accessibility Service to read visible text — you control everything."),
        Pair("Privacy First", "We process text locally. No screenshots uploaded. You remain in control."),
        Pair("Ready!", "Set your rules and start matching. You always confirm the final action."),
    )

    val (title, desc) = pages[page]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val icon = when (page) {
            0 -> Icons.Default.DirectionsCar
            1 -> Icons.Default.AutoMode
            2 -> Icons.Default.Translate
            3 -> Icons.Default.AttachMoney
            4 -> Icons.Default.PictureInPicture
            5 -> Icons.Default.Accessibility
            6 -> Icons.Default.Security
            else -> Icons.Default.CheckCircle
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
