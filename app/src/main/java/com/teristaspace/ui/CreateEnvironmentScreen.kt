package com.teristaspace.ui

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.teristaspace.core.VirtualEnvironmentManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEnvironmentScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: CreateEnvironmentViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VirtualEnvironmentManager.EnvironmentType.VIRTUAL_DEVICE) }
    var isCreating by remember { mutableStateOf(false) }

    val supportedTypes = VirtualEnvironmentManager.getSupportedTypes()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Create Virtual Environment",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Environment Name") },
            placeholder = { Text("e.g., Work Profile, Gaming Space") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Environment Type",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom: 8.dp)
        )

        supportedTypes.forEach { type ->
            EnvironmentTypeCard(
                type = type,
                isSelected = selectedType == type,
                onClick = { selectedType = type }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        InfoCard(selectedType)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isCreating = true
                viewModel.createEnvironment(name, selectedType) {
                    isCreating = false
                    onNavigateBack()
                }
            },
            enabled = name.isNotBlank() && !isCreating,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isCreating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Environment")
            }
        }
    }
}

@Composable
fun EnvironmentTypeCard(
    type: VirtualEnvironmentManager.EnvironmentType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (icon, title, description) = when (type) {
        VirtualEnvironmentManager.EnvironmentType.VIRTUAL_DEVICE -> Triple(
            Icons.Default.PhoneAndroid,
            "Virtual Device",
            "Full Android device virtualization (Android 14+)"
        )
        VirtualEnvironmentManager.EnvironmentType.WORK_PROFILE -> Triple(
            Icons.Default.Work,
            "Work Profile",
            "Enterprise-grade app isolation"
        )
        VirtualEnvironmentManager.EnvironmentType.VIRTUAL_DISPLAY -> Triple(
            Icons.Default.DesktopWindows,
            "Virtual Display",
            "Separate screen space for apps"
        )
        VirtualEnvironmentManager.EnvironmentType.PRIVATE_SPACE -> Triple(
            Icons.Default.Lock,
            "Private Space",
            "Android 15+ secure container with authentication"
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InfoCard(type: VirtualEnvironmentManager.EnvironmentType) {
    val message = when (type) {
        VirtualEnvironmentManager.EnvironmentType.VIRTUAL_DEVICE ->
            "Uses Android 14+ VirtualDeviceManager API. Creates a virtual Android device with its own apps and data."
        VirtualEnvironmentManager.EnvironmentType.WORK_PROFILE ->
            "Creates a work profile for app isolation. Apps in work profile are separate from personal apps."
        VirtualEnvironmentManager.EnvironmentType.VIRTUAL_DISPLAY ->
            "Creates a virtual screen where apps can run independently. Useful for multitasking."
        VirtualEnvironmentManager.EnvironmentType.PRIVATE_SPACE ->
            "Android 15+ feature. Creates a secure, password-protected space for sensitive apps."
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
