package pro.udeedit.devtools.pushestest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import pro.udeedit.devtools.cushystorage.CushyStorage

/**
 * Base Application class for the Pushes Test utility.
 *
 * This class serves as the entry point for dependency injection and
 * global library initialization.
 *
 * Annotating with [@HiltAndroidApp] triggers Hilt's code generation,
 * including a base class that serves as the application-level dependency
 * container for the entire project.
 */
@HiltAndroidApp
class PtApp : Application() {

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * Initialization steps:
     * - Initialize [CushyStorage] to provide immediate access to local
     *    preferences throughout the app lifecycle.
     */
    override fun onCreate() {
        super.onCreate()

        // Initialize the internal persistence module
        CushyStorage.init(this)
    }
}
