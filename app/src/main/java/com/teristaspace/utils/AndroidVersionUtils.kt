package com.teristaspace.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object AndroidVersionUtils {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.BAKLAVA)
    fun isAtLeastBaklava(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA
    }

    fun isAtLeastBaklavaQPR1(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            try {
                val sdkIntFull = Build.VERSION.SDK_INT_FULL
                sdkIntFull >= Build.VERSION_CODES_FULL.BAKLAVA_1
            } catch (e: NoSuchFieldError) {
                false
            }
        } else {
            false
        }
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun isAtLeastVanillaIceCream(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun isAtLeastUpsideDownCake(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    }

    fun supportsVirtualDeviceManager(): Boolean {
        return isAtLeastUpsideDownCake()
    }

    fun supportsPrivateSpace(): Boolean {
        return isAtLeastVanillaIceCream()
    }

    fun supportsAdvancedVirtualization(): Boolean {
        return isAtLeastBaklava()
    }

    fun getAndroidVersionName(): String {
        return when {
            isAtLeastBaklava() -> "Android 16 Baklava"
            isAtLeastVanillaIceCream() -> "Android 15 Vanilla Ice Cream"
            isAtLeastUpsideDownCake() -> "Android 14 Upside Down Cake"
            else -> "Android ${Build.VERSION.RELEASE}"
        }
    }

    fun getDetailedVersionInfo(): String {
        val baseVersion = getAndroidVersionName()
        val minorVersion = if (isAtLeastBaklavaQPR1()) " (QPR1)" else ""
        return "$baseVersion$minorVersion (API ${Build.VERSION.SDK_INT})"
    }
}
