package com.teristaspace.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "Version",
                subtitle = "1.0.0 (Android 15/16 Compatible)"
            )
            SettingsItem(
                icon = Icons.Default.Security,
                title = "Security Status",
                subtitle = "Using Official Android APIs Only"
            )
            SettingsItem(
                icon = Icons.Default.Verified,
                title = "License",
                subtitle = "Open Source - Apache 2.0"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(title = "Features") {
            SettingsItem(
                icon = Icons.Default.PhoneAndroid,
                title = "Virtual Device Manager",
                subtitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                    "Available (Android 14+)" else "Requires Android 14+"
            )
            SettingsItem(
                icon = Icons.Default.Work,
                title = "Work Profile",
                subtitle = "Available"
            )
            SettingsItem(
                icon = Icons.Default.DesktopWindows,
                title = "Virtual Display",
                subtitle = "Available"
            )
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Private Space",
                subtitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    "Available (Android 15+)" else "Requires Android 15+"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(title = "Help & Support") {
            SettingsItem(
                icon = Icons.Default.Help,
                title = "Documentation",
                subtitle = "View usage guide",
                onClick = {}
            )
            SettingsItem(
                icon = Icons.Default.BugReport,
                title = "Report Issue",
                subtitle = "GitHub Issues",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/yourusername/teristaspace/issues"))
                    context.startActivity(intent)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Legal Notice",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "TeristaSpace uses only official Android APIs (VirtualDeviceManager, Work Profile, Private Space) provided by Google. No system-level hacking, Binder hooking, or IMEI spoofing is used.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Card {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
