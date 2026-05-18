package pro.udeedit.devtools.pushestest

import android.app.Application
import android.util.Log
import pro.udeedit.devtools.cushystorage.CushyStorage

private const val TAG = "PtApp"

class PtApp : Application() {


    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        CushyStorage.init(this)
    }
}