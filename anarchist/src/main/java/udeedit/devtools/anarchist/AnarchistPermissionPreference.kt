package udeedit.devtools.anarchist

import android.content.Context
import androidx.core.content.edit

internal class AnarchistPermissionPreference(context: Context) {

    companion object {
        private const val PERMISSION_PREFERENCE_FILE = "permissionPreference"
    }

    private val sharedPreference =
        context.getSharedPreferences(PERMISSION_PREFERENCE_FILE, Context.MODE_PRIVATE)

    fun isPermissionRequestedBefore(permission: String): Boolean {
        return sharedPreference.getBoolean(permission, false)
    }

    fun setPermissionRequested(permission: String) {
        sharedPreference.edit {
            putBoolean(permission, true)
        }
    }

    fun setPermissionAllowed(permission: String) {
        sharedPreference.edit {
            putBoolean(permission, false)
        }
    }

}