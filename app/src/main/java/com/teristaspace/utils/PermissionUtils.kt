package com.teristaspace.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    val VIRTUAL_DEVICE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(
            Manifest.permission.BIND_COMPANION_DEVICE_SERVICE
        )
    } else {
        emptyArray()
    }

    val WORK_PROFILE_PERMISSIONS = arrayOf(
        Manifest.permission.MANAGE_DEVICE_POLICY_WORK_PROFILE
    )

    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun getRequiredPermissionsForFeature(feature: VirtualFeature): Array<String> {
        return when (feature) {
            VirtualFeature.VIRTUAL_DEVICE -> VIRTUAL_DEVICE_PERMISSIONS
            VirtualFeature.WORK_PROFILE -> WORK_PROFILE_PERMISSIONS
            VirtualFeature.VIRTUAL_DISPLAY -> emptyArray()
            VirtualFeature.PRIVATE_SPACE -> emptyArray()
        }
    }

    enum class VirtualFeature {
        VIRTUAL_DEVICE,
        WORK_PROFILE,
        VIRTUAL_DISPLAY,
        PRIVATE_SPACE
    }
}
