package com.teristaspace.service

import android.companion.CompanionDeviceService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class VirtualDeviceService : CompanionDeviceService() {

    override fun onDeviceAppeared(associationInfo: android.companion.AssociationInfo) {
        super.onDeviceAppeared(associationInfo)
    }

    override fun onDeviceDisappeared(associationInfo: android.companion.AssociationInfo) {
        super.onDeviceDisappeared(associationInfo)
    }

    override fun onBind(intent: Intent?): android.os.IBinder? {
        return super.onBind(intent)
    }
}
