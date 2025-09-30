package pro.udeedit.devtools.pushestest

import android.app.Application
import android.util.Log

private const val TAG = "NTT_App"

// NTT key - Notification Test Tool
class NttApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }
}