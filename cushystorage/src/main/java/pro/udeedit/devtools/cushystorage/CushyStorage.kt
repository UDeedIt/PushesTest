package pro.udeedit.devtools.cushystorage

import android.app.Application
import android.content.SharedPreferences
import pro.udeedit.devtools.cushystorage.extension.defaultPrefs

class CushyStorage {

    companion object DefaultPrefs {
        private lateinit var preferences: SharedPreferences

        fun init(context: Application) {
//            <context.getPackageName()> + "_preferences"
//            /data/data/com.example.app/shared_prefs/
//            preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            preferences = context.defaultPrefs()
        }

        /**
         * SharedPreferences extension function, so we won't need to call edit() and apply()
         * ourselves on every SharedPreferences operation.
         */
        private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
            val editor = edit()
            operation(editor)
            editor.apply()
        }


        fun saveBoolean(key: String, value: Boolean) {
            preferences.edit {
                it.putBoolean(key, value)
            }
        }

        fun getBoolean(key: String, defaultValue: Boolean): Boolean {
            return preferences.getBoolean(key, defaultValue)
        }


        fun saveString(key: String, value: String) {
            preferences.edit {
                it.putString(key, value)
            }
        }

        fun getString(key: String, defaultValue: String): String? {
            return preferences.getString(key, defaultValue)
        }


        fun saveInt(key: String, value: Int) {
            preferences.edit {
                it.putInt(key, value)
            }
        }

        fun getInt(key: String, defaultValue: Int): Int {
            return preferences.getInt(key, defaultValue)
        }


        fun saveLong(key: String, value: Long) {
            preferences.edit {
                it.putLong(key, value)
            }
        }

        fun getLong(key: String, defaultValue: Long): Long {
            return preferences.getLong(key, defaultValue)
        }


        fun hasValue(toString: String): Boolean {
            return preferences.contains(toString)
        }
    }
}