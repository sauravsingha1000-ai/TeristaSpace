package com.teristaspace.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.teristaspace.core.VirtualEnvironmentManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(
    viewModel: AppsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val apps by viewModel.installedApps.collectAsState()
    val environments by viewModel.environments.collectAsState()
    val selectedEnvironment by viewModel.selectedEnvironment.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Environment",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        EnvironmentDropdown(
            environments = environments,
            selected = selectedEnvironment,
            onSelect = { viewModel.selectEnvironment(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Installed Apps",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (apps.isEmpty()) {
            EmptyAppsState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(apps) { app ->
                    AppCard(
                        appInfo = app,
                        onLaunch = { viewModel.launchApp(app.packageName) },
                        onClone = { viewModel.cloneAppToEnvironment(app.packageName) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentDropdown(
    environments: List<VirtualEnvironmentManager.VirtualEnvironment>,
    selected: VirtualEnvironmentManager.VirtualEnvironment?,
    onSelect: (VirtualEnvironmentManager.VirtualEnvironment) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.name ?: "Select an environment",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            environments.forEach { environment ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(environment.name)
                            Text(
                                environment.type.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onSelect(environment)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCard(
    appInfo: AppInfo,
    onLaunch: () -> Unit,
    onClone: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = appInfo.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(onClick = onClone) {
                    Icon(
                        imageVector = Icons.Default.CopyAll,
                        contentDescription = "Clone to Environment"
                    )
                }
                IconButton(onClick = onLaunch) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Launch"
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyAppsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Apps,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Apps Found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Install apps on your device to see them here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class AppInfo(
    val name: String,
    val packageName: String,
    val isSystemApp: Boolean = false
)
