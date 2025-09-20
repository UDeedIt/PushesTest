package udeedit.tools.notificationtest

import android.app.Application
import android.util.Log

private const val TAG = "HeadUpApp"

// NTT key - Notification Test Tool
class NttApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }
}