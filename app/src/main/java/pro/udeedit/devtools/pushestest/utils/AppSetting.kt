package pro.udeedit.devtools.pushestest.utils

/**
 * Defines the complete set of configurable parameters for the Pushes Test application.
 *
 * In this MVI (Model-View-Intent) architecture, this Enum acts as the primary
 * "Intent" identifier. Every user interaction in the Settings screen—whether
 * toggling a switch or selecting a dropdown item—is mapped to one of these
 * constants and processed by a unified handler in the ViewModel.
 */
enum class AppSetting {
    // BEHAVIORAL SETTINGS (Boolean)

    /** Toggles the automatic generation of developer-themed payloads. */
    MOCK_DATA,

    /** Determines if new notifications should replace existing ones using a fixed ID. */
    OVERWRITE,

    /** Applies the "Ongoing" flag to prevent standard dismissal. */
    PERSISTENT,

    /** Toggles the expanded height of the manual input field in the UI. */
    MULTILINE,

    /** Enables high-priority alerts that wake the device and launch the activity. */
    FULL_SCREEN,

    /** Bundles notifications into a stack using a common group key. */
    GROUPED,

    // VISUAL STYLE SETTINGS (Boolean)

    /** Enables the [androidx.core.app.NotificationCompat.BigTextStyle]. */
    BIG_TEXT,

    /** Enables the [androidx.core.app.NotificationCompat.BigPictureStyle]. */
    BIG_PICTURE,

    /** Enables the [androidx.core.app.NotificationCompat.InboxStyle]. */
    INBOX_STYLE,

    /** Adds interactive buttons to the notification drawer. */
    ACTIONS,

    /** Toggles supplementary header metadata. */
    SUBTEXT,

    /** Toggles the side-aligned thumbnail icon. */
    LARGE_ICON,

    /** Adds a live, system-managed timer to the notification. */
    CHRONOMETER,

    // SENSORY SETTINGS (Boolean)

    /** Enables haptic feedback for both button interactions and arrival events. */
    VIBRATION,

    /** Toggles the default system notification sound. */
    SOUND,

    // CONFIGURATION SETTINGS (Integer/Dropdown)

    /** The index of the selected repeating interval. */
    PERIODS,

    /** The index of the selected scheduled delay. */
    DELAYS,

    /** The index of the target Notification Channel (Urgent to Low). */
    IMPORTANCE,

    /** The index of the desired lock-screen privacy level (Public to Secret). */
    VISIBILITY
}
