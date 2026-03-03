package com.subin.leafy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.ui.screen.EntryPointScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var pendingDeepLink by mutableStateOf<Any?>(null)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM_LOG", "알림 권한 허용됨")
        } else {
            Log.d("FCM_LOG", "알림 권한 거부됨")
        }

        mainViewModel.syncNotificationSetting(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        askNotificationPermission()

        handleDeepLink(intent)

        splashScreen.setKeepOnScreenCondition {
            mainViewModel.isSplashLoading.value || mainViewModel.startDestination.value == null
        }

        enableEdgeToEdge()

        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())

        setContent {
            LeafyTheme {
                val startDestination by mainViewModel.startDestination.collectAsStateWithLifecycle()

                if (startDestination != null) {
                    EntryPointScreen(
                        startDestination = startDestination!!,
                        pendingDeepLink = pendingDeepLink,
                        onDeepLinkConsumed = { pendingDeepLink = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.let {
            val type = it.getStringExtra("type")
            val targetPostId = it.getStringExtra("targetPostId")
            val senderId = it.getStringExtra("senderId")

            if (type.isNullOrBlank()) return@let

            if (type.uppercase() == "LIKE" || type.uppercase() == "COMMENT") {
                if (!targetPostId.isNullOrBlank()) {
                    pendingDeepLink = MainNavigationRoute.CommunityDetail(targetPostId)
                }
            } else if (type.uppercase() == "FOLLOW") {
                if (!senderId.isNullOrBlank()) {
                    pendingDeepLink = MainNavigationRoute.UserProfile(senderId)
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}