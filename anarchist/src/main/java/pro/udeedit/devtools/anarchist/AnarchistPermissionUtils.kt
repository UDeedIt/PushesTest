package pro.udeedit.devtools.anarchist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AnarchistPermissionUtils {

    companion object {

        fun checkAndRequestPermissions(
            activity: Activity,
            permissions: MutableList<String>,
            requestCode: Int,
            checkStatusOnly: Boolean = false

        ): AnarchistPermissionResult {

            val permissionPreference = AnarchistPermissionPreference(activity)
            val permissionResult = AnarchistPermissionResult()
            val permissionStatus: HashMap<String, AnarchistPermissionStatus> = hashMapOf()

            permissions.forEach { permission ->
                if (hasPermissionAllowed(activity, permission)) {
                    permissionPreference.setPermissionAllowed(permission)
                    permissionStatus[permission] = AnarchistPermissionStatus.ALLOWED

                } else {
                    val isShowRationale = isNeededToShowRequestRationale(activity, permission)
                    val isAskedPermissionBefore = permissionPreference.isPermissionRequestedBefore(permission)

                    when {
                        isShowRationale -> {
                            permissionStatus[permission] = AnarchistPermissionStatus.DENIED
                        }

                        isAskedPermissionBefore && !isShowRationale -> {
                            permissionStatus[permission] = AnarchistPermissionStatus.DENIED_PERMANENTLY
                        }

                        else -> {
                            permissionStatus[permission] = AnarchistPermissionStatus.DENIED
                        }
                    }
                }
            }

            permissionResult.permissionStatus = permissionStatus

            val isAnyPermissionDeniedPermanently = permissionStatus.values.any {
                it == AnarchistPermissionStatus.DENIED_PERMANENTLY
            }

            if (isAnyPermissionDeniedPermanently) {
                permissionResult.finalStatus = AnarchistPermissionStatus.DENIED_PERMANENTLY
                return permissionResult
            }

            val isAnyPermissionNotGiven = permissionStatus.values.any {
                it == AnarchistPermissionStatus.DENIED
            }

            if (isAnyPermissionNotGiven) {
                if (!checkStatusOnly) {
                    val notGivenPermissionList = permissionStatus.filter {
                        it.value == AnarchistPermissionStatus.DENIED

                    }.keys.toMutableList()

                    requestPermissions(activity, notGivenPermissionList, requestCode)
                }

                permissionResult.finalStatus = AnarchistPermissionStatus.DENIED
                return permissionResult
            }

            permissionResult.finalStatus = AnarchistPermissionStatus.ALLOWED
            return permissionResult
        }

        fun checkAndRequestPermission(
            activity: Activity,
            permission: String,
            requestCode: Int,
            checkStatusOnly: Boolean = false

        ): AnarchistPermissionResult {
            val permissionList: MutableList<String> = mutableListOf()
            permissionList.add(permission)

            return checkAndRequestPermissions(
                activity,
                permissionList,
                requestCode,
                checkStatusOnly
            )
        }

        fun askUserToRequestPermissionExplicitly(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }

        private fun hasPermissionAllowed(activity: Activity, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        private fun isNeededToShowRequestRationale(activity: Activity, permission: String): Boolean {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }


        private fun requestPermissions(
            activity: Activity,
            permissionList: MutableList<String>,
            requestCode: Int
        ) {
            activity.requestPermissions(permissionList.toTypedArray(), requestCode)

            val permissionPreference = AnarchistPermissionPreference(activity)

            for (permission in permissionList) {
                permissionPreference.setPermissionRequested(permission)
            }
        }
    }
}