package com.teristaspace.core

import android.app.Activity
import android.app.PendingIntent
import android.companion.AssociationRequest
import android.companion.CompanionDeviceManager
import android.companion.VirtualDeviceFilter
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VirtualEnvironmentManager(private val context: Context) {

    private val _environments = MutableStateFlow<List<VirtualEnvironment>>(emptyList())
    val environments: StateFlow<List<VirtualEnvironment>> = _environments

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private val companionDeviceManager = context.getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager

    private val virtualDisplays = mutableMapOf<String, VirtualDisplay>()
    private val handler = Handler(Looper.getMainLooper())

    data class VirtualEnvironment(
        val id: String,
        val name: String,
        val type: EnvironmentType,
        val status: EnvironmentStatus,
        val displayId: Int? = null,
        val createdAt: Long = System.currentTimeMillis()
    )

    enum class EnvironmentType {
        WORK_PROFILE,
        VIRTUAL_DEVICE,
        VIRTUAL_DISPLAY,
        PRIVATE_SPACE
    }

    enum class EnvironmentStatus {
        CREATING, RUNNING, PAUSED, STOPPED, ERROR
    }

    fun createEnvironment(name: String, type: EnvironmentType = EnvironmentType.VIRTUAL_DEVICE): Result<String> {
        return try {
            val id = "env_${System.currentTimeMillis()}"

            when (type) {
                EnvironmentType.VIRTUAL_DEVICE -> createVirtualDeviceEnvironment(id, name)
                EnvironmentType.WORK_PROFILE -> createWorkProfileEnvironment(id, name)
                EnvironmentType.VIRTUAL_DISPLAY -> createVirtualDisplayEnvironment(id, name)
                EnvironmentType.PRIVATE_SPACE -> createPrivateSpaceEnvironment(id, name)
            }

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createVirtualDeviceEnvironment(id: String, name: String) {
        val request = AssociationRequest.Builder()
            .addDeviceFilter(VirtualDeviceFilter.Builder().build())
            .setSingleDevice(true)
            .build()

        addEnvironmentToList(VirtualEnvironment(
            id = id,
            name = name,
            type = EnvironmentType.VIRTUAL_DEVICE,
            status = EnvironmentStatus.CREATING
        ))
    }

    private fun createWorkProfileEnvironment(id: String, name: String) {
        addEnvironmentToList(VirtualEnvironment(
            id = id,
            name = name,
            type = EnvironmentType.WORK_PROFILE,
            status = EnvironmentStatus.CREATING
        ))
    }

    private fun createVirtualDisplayEnvironment(id: String, name: String) {
        val metrics = context.resources.displayMetrics

        val virtualDisplay = displayManager.createVirtualDisplay(
            "TeristaSpace_$id",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            null,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
        )

        virtualDisplays[id] = virtualDisplay

        addEnvironmentToList(VirtualEnvironment(
            id = id,
            name = name,
            type = EnvironmentType.VIRTUAL_DISPLAY,
            status = EnvironmentStatus.RUNNING,
            displayId = virtualDisplay.display.displayId
        ))
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun createPrivateSpaceEnvironment(id: String, name: String) {
        addEnvironmentToList(VirtualEnvironment(
            id = id,
            name = name,
            type = EnvironmentType.PRIVATE_SPACE,
            status = EnvironmentStatus.CREATING
        ))
    }

    fun stopEnvironment(id: String) {
        virtualDisplays[id]?.release()
        virtualDisplays.remove(id)
        updateEnvironmentStatus(id, EnvironmentStatus.STOPPED)
    }

    fun launchAppInEnvironment(environmentId: String, packageName: String): Result<Unit> {
        return try {
            val env = _environments.value.find { it.id == environmentId }
                ?: return Result.failure(IllegalStateException("Environment not found"))

            when (env.type) {
                EnvironmentType.VIRTUAL_DISPLAY -> {
                    env.displayId?.let { displayId ->
                        launchAppOnDisplay(packageName, displayId)
                    }
                }
                else -> {
                    launchAppInProfile(packageName, env)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun launchAppOnDisplay(packageName: String, displayId: Int) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            ?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            }
        intent?.let { context.startActivity(it) }
    }

    private fun launchAppInProfile(packageName: String, env: VirtualEnvironment) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let { context.startActivity(it) }
    }

    private fun addEnvironmentToList(env: VirtualEnvironment) {
        _environments.value += env
    }

    private fun updateEnvironmentStatus(id: String, status: EnvironmentStatus) {
        _environments.value = _environments.value.map {
            if (it.id == id) it.copy(status = status) else it
        }
    }

    fun cleanup() {
        virtualDisplays.values.forEach { it.release() }
        virtualDisplays.clear()
    }

    companion object {
        fun isSupported(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        }

        fun getSupportedTypes(): List<EnvironmentType> {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM -> {
                    EnvironmentType.values().toList()
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    listOf(
                        EnvironmentType.VIRTUAL_DEVICE,
                        EnvironmentType.WORK_PROFILE,
                        EnvironmentType.VIRTUAL_DISPLAY
                    )
                }
                else -> {
                    listOf(EnvironmentType.WORK_PROFILE, EnvironmentType.VIRTUAL_DISPLAY)
                }
            }
        }
    }
}

class VirtualDeviceCallbackActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}

