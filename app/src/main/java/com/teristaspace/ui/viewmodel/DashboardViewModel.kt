package com.teristaspace.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.teristaspace.core.VirtualEnvironmentManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val virtualEnvManager = VirtualEnvironmentManager(application)

    private val _environments = MutableStateFlow<List<VirtualEnvironmentManager.VirtualEnvironment>>(emptyList())
    val environments: StateFlow<List<VirtualEnvironmentManager.VirtualEnvironment>> = _environments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadEnvironments()
    }

    private fun loadEnvironments() {
        viewModelScope.launch {
            _isLoading.value = true
            _environments.value = virtualEnvManager.environments.value
            _isLoading.value = false
        }
    }

    fun launchEnvironment(id: String) {
        viewModelScope.launch {
            // Launch environment logic
        }
    }

    fun stopEnvironment(id: String) {
        viewModelScope.launch {
            virtualEnvManager.stopEnvironment(id)
            loadEnvironments()
        }
    }

    fun refresh() {
        loadEnvironments()
    }

    override fun onCleared() {
        super.onCleared()
        virtualEnvManager.cleanup()
    }
}
