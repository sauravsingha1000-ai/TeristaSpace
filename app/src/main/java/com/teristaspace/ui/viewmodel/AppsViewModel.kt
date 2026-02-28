package com.teristaspace.ui.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.teristaspace.core.VirtualEnvironmentManager
import com.teristaspace.ui.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppsViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager
    private val virtualEnvManager = VirtualEnvironmentManager(application)

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    private val _environments = MutableStateFlow<List<VirtualEnvironmentManager.VirtualEnvironment>>(emptyList())
    val environments: StateFlow<List<VirtualEnvironmentManager.VirtualEnvironment>> = _environments

    private val _selectedEnvironment = MutableStateFlow<VirtualEnvironmentManager.VirtualEnvironment?>(null)
    val selectedEnvironment: StateFlow<VirtualEnvironmentManager.VirtualEnvironment?> = _selectedEnvironment

    init {
        loadApps()
        loadEnvironments()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                .map { appInfo ->
                    AppInfo(
                        name = packageManager.getApplicationLabel(appInfo).toString(),
                        packageName = appInfo.packageName,
                        isSystemApp = false
                    )
                }
                .sortedBy { it.name }
            _installedApps.value = apps
        }
    }

    private fun loadEnvironments() {
        viewModelScope.launch {
            _environments.value = virtualEnvManager.environments.value
        }
    }

    fun selectEnvironment(env: VirtualEnvironmentManager.VirtualEnvironment) {
        _selectedEnvironment.value = env
    }

    fun launchApp(packageName: String) {
        viewModelScope.launch {
            _selectedEnvironment.value?.let { env ->
                virtualEnvManager.launchAppInEnvironment(env.id, packageName)
            }
        }
    }

    fun cloneAppToEnvironment(packageName: String) {
        viewModelScope.launch {
            // Clone app logic
        }
    }
}
