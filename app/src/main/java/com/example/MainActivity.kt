package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.example.data.service.PushNotificationManager
import com.example.ui.screens.MainNavigationScreen
import com.example.ui.theme.ZubZeroTheme
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ZubZeroViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ZubZeroViewModel by viewModels()

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.onNotificationPermissionResult(isGranted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkNotificationPermission()
        handlePushNotificationIntent(intent)

        setContent {
            ZubZeroTheme {
                MainNavigationScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePushNotificationIntent(intent)
    }

    private fun handlePushNotificationIntent(intent: Intent?) {
        val targetTabName = intent?.getStringExtra(PushNotificationManager.EXTRA_NAV_TAB)
        if (!targetTabName.isNullOrBlank()) {
            val destination = when (targetTabName.uppercase()) {
                "MATCHMAKING" -> AppNavTab.MATCHMAKING
                "MARKETPLACE" -> AppNavTab.MARKETPLACE
                "MY_HUB" -> AppNavTab.MY_HUB
                "AI_STUDIO" -> AppNavTab.AI_STUDIO
                "CINEMA_THEATER" -> AppNavTab.CINEMA_THEATER
                "HOTELS" -> AppNavTab.HOTELS
                "FASHION" -> AppNavTab.FASHION
                else -> AppNavTab.HOME
            }
            viewModel.setTab(destination)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            viewModel.onNotificationPermissionResult(hasPermission)

            if (!hasPermission) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.onNotificationPermissionResult(true)
        }
    }
}

@Composable
fun MainContent(viewModel: ZubZeroViewModel) {
    MainNavigationScreen(viewModel = viewModel)
}
