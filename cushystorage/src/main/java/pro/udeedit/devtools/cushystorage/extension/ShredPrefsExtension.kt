package pro.udeedit.devtools.cushystorage.extension

import android.content.Context
import android.content.SharedPreferences


fun Context.defaultPrefs(name: String = packageName + "_preferences"): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)