package pro.udeedit.devtools.pushestest

import android.app.Application
import android.util.Log

private const val TAG = "PT_App"

class PtApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }
}