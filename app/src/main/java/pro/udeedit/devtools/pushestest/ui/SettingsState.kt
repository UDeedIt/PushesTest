package pro.udeedit.devtools.pushestest.ui

import pro.udeedit.devtools.pushestest.utils.*

/**
 * An immutable snapshot of the entire UI state for the Pushes Test screen.
 *
 * This data class is the "Model" in the MVI (Model-View-Intent) architecture.
 * It centralizes every user preference, input field value, and active system
 * state (such as periodic loops).
 *
 * Benefits:
 * - Unidirectional Data Flow (UDF): Ensures the UI is a pure reflection of the state.
 * - Preview Stability: Allows Android Studio to render the UI instantly without
 *   requiring a real device or initialized ViewModel.
 * - Thread Safety: Being a data class, it is used with the [.copy()] method
 *   to ensure atomic updates to the UI.
 */
data class SettingsState(
    /** The current text in the notification title field. */
    val notificationTitle: String = "",

    /** The current text in the notification body field. */
    val notificationBody: String = "",

    /** Indicates if the automatic mock data generation mode is active. */
    val isMockEnabled: Boolean = DEF_USE_MOCK_DATA,

    /** If true, new notifications will use a fixed ID (0) to overwrite existing ones. */
    val isOverwrite: Boolean = DEF_OVERWRITE_NOTIFICATION,

    /** Sets the 'Ongoing' flag, prioritizing the notification in the tray. */
    val isPersistent: Boolean = DEF_IS_PERSISTENT,

    /** Toggles the expanded (multi-line) height of the input field in the UI. */
    val isMultiline: Boolean = DEF_MULTILINE_NOTIFICATION,

    /** Enables high-priority alerts that launch the app UI over the lock screen. */
    val isFullScreen: Boolean = DEF_FULL_SCREEN,

    /** Bundles multiple notifications into a stack using a common group key. */
    val isGrouped: Boolean = DEF_GROUPED_NOTIFICATIONS,

    /** Indicates if a continuous notification loop is currently running. */
    val isPeriodicActive: Boolean = DEF_IS_PERIODIC_ACTIVE,

    /** The current selection index for repeating notification intervals. */
    val periodsPos: Int = DEF_PERIODS_POS,

    /** The current selection index for scheduled notification delays. */
    val delaysPos: Int = DEF_DELAYS_POS,

    /** Toggles the use of the expandable [androidx.core.app.NotificationCompat.BigTextStyle]. */
    val useBigText: Boolean = DEF_USE_BIG_TEXT,

    /** Toggles the use of [androidx.core.app.NotificationCompat.BigPictureStyle] for hero images. */
    val showBigPicture: Boolean = DEF_SHOW_BIG_PICTURE,

    /** Toggles the use of [androidx.core.app.NotificationCompat.InboxStyle] for list previews. */
    val useInboxStyle: Boolean = DEF_USE_INBOX_STYLE,

    /** Determines if interactive buttons are added to the notification drawer. */
    val includeActions: Boolean = DEF_INCLUDE_ACTIONS,

    /** Toggles the display of the application name and hints in the header metadata. */
    val showSubtext: Boolean = DEF_SHOW_SUBTEXT,

    /** Toggles the side-aligned circular thumbnail icon. */
    val showLargeIcon: Boolean = DEF_SHOW_LARGE_ICON,

    /** Adds a system-managed live timer to the notification card. */
    val useChronometer: Boolean = DEF_CHRONOMETER,

    /** Global switch for haptic feedback during interactions and arrival. */
    val vibrationOn: Boolean = DEF_VIBRATION_ON,

    /** Toggles the default system sound for the notification channel. */
    val enableSound: Boolean = DEF_ENABLE_SOUND,

    /** The importance level assigned to the active notification channel. */
    val importancePos: Int = DEF_IMPORTANCE_POS,

    /** Privacy setting for content visibility on a secure lock screen. */
    val visibilityPos: Int = DEF_VISIBILITY_POS
)
