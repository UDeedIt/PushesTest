package pro.udeedit.devtools.pushestest.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object PtLocaleUtils {

    /**
     * Retrieves a string resource in English, regardless of the current system language.
     * Useful for system-level configurations like Notification Channel names.
     */
    fun getEnglishString(context: Context, resId: Int): String {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(Locale.ENGLISH)

        // Create a temporary context with the English configuration
        val englishContext = context.createConfigurationContext(configuration)
        return englishContext.getString(resId)
    }

    /**
     * Retrieves a string array in English.
     */
    fun getEnglishStringArray(context: Context, resId: Int): Array<String> {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(Locale.ENGLISH)
        val englishContext = context.createConfigurationContext(configuration)
        return englishContext.resources.getStringArray(resId)
    }

}
