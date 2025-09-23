package udeedit.tools.notificationtest.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object PermissionsUtil {

    const val TAG = "PermissionsUtil"

    fun checkSendNotificationsPermission(context: Context): Boolean {
        return (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
    }
}