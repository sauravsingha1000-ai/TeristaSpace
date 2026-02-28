package com.teristaspace.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.teristaspace.core.VirtualEnvironmentManager
import kotlinx.coroutines.launch

class CreateEnvironmentViewModel(application: Application) : AndroidViewModel(application) {

    private val virtualEnvManager = VirtualEnvironmentManager(application)

    fun createEnvironment(
        name: String,
        type: VirtualEnvironmentManager.EnvironmentType,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            virtualEnvManager.createEnvironment(name, type)
            onComplete()
        }
    }
}
