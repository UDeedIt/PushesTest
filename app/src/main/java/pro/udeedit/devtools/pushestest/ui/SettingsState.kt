package pro.udeedit.devtools.pushestest.ui

import pro.udeedit.devtools.pushestest.utils.DEF_CHRONOMETER
import pro.udeedit.devtools.pushestest.utils.DEF_DELAYS_POS
import pro.udeedit.devtools.pushestest.utils.DEF_ENABLE_SOUND
import pro.udeedit.devtools.pushestest.utils.DEF_FULL_SCREEN
import pro.udeedit.devtools.pushestest.utils.DEF_GROUPED_NOTIFICATIONS
import pro.udeedit.devtools.pushestest.utils.DEF_IMPORTANCE_POS
import pro.udeedit.devtools.pushestest.utils.DEF_INCLUDE_ACTIONS
import pro.udeedit.devtools.pushestest.utils.DEF_IS_PERSISTENT
import pro.udeedit.devtools.pushestest.utils.DEF_MULTILINE_NOTIFICATION
import pro.udeedit.devtools.pushestest.utils.DEF_OVERWRITE_NOTIFICATION
import pro.udeedit.devtools.pushestest.utils.DEF_PERIODS_POS
import pro.udeedit.devtools.pushestest.utils.DEF_SHOW_BIG_PICTURE
import pro.udeedit.devtools.pushestest.utils.DEF_SHOW_LARGE_ICON
import pro.udeedit.devtools.pushestest.utils.DEF_SHOW_SUBTEXT
import pro.udeedit.devtools.pushestest.utils.DEF_USE_BIG_TEXT
import pro.udeedit.devtools.pushestest.utils.DEF_USE_INBOX_STYLE
import pro.udeedit.devtools.pushestest.utils.DEF_USE_MOCK_DATA
import pro.udeedit.devtools.pushestest.utils.DEF_VIBRATION_ON
import pro.udeedit.devtools.pushestest.utils.DEF_VISIBILITY_POS

/**
 * Represents the entire UI state of the Pushes Test screen.
 * This simple data class allows for instant, crash-free Compose Previews.
 */
data class SettingsState(
    val notificationTitle: String = "",
    val notificationBody: String = "",
    val isMockEnabled: Boolean = DEF_USE_MOCK_DATA,
    val isOverwrite: Boolean = DEF_OVERWRITE_NOTIFICATION,
    val isPersistent: Boolean = DEF_IS_PERSISTENT,
    val isMultiline: Boolean = DEF_MULTILINE_NOTIFICATION,
    val isFullScreen: Boolean = DEF_FULL_SCREEN,
    val isGrouped: Boolean = DEF_GROUPED_NOTIFICATIONS,
    val isPeriodicActive: Boolean = false,
    val periodsPos: Int = DEF_PERIODS_POS,
    val delaysPos: Int = DEF_DELAYS_POS,
    val useBigText: Boolean = DEF_USE_BIG_TEXT,
    val showBigPicture: Boolean = DEF_SHOW_BIG_PICTURE,
    val useInboxStyle: Boolean = DEF_USE_INBOX_STYLE,
    val includeActions: Boolean = DEF_INCLUDE_ACTIONS,
    val showSubtext: Boolean = DEF_SHOW_SUBTEXT,
    val showLargeIcon: Boolean = DEF_SHOW_LARGE_ICON,
    val useChronometer: Boolean = DEF_CHRONOMETER,
    val vibrationOn: Boolean = DEF_VIBRATION_ON,
    val enableSound: Boolean = DEF_ENABLE_SOUND,
    val importancePos: Int = DEF_IMPORTANCE_POS,
    val visibilityPos: Int = DEF_VISIBILITY_POS
)
