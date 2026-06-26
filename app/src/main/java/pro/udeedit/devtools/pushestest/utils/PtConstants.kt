package pro.udeedit.devtools.pushestest.utils

/**
 * Centralized constant definitions for the Pushes Test application.
 *
 * This file maintains all global identifiers, shared preference keys, and
 * factory default values. Centralizing these ensures consistency between
 * the [pro.udeedit.devtools.cushystorage] persistence layer and the UI state.
 */

// SYSTEM IDENTIFIERS

/** Request code used for runtime permission dialogues. */
const val REQUEST_PERMISSION_CODE = 100

/** Base ID for the application's notification channels. */
const val CHANNEL_ID = "pro.udeedit.devtools.pushestest.channel_id"

/** Shared key for bundling notifications into logical groups. */
const val GROUP_KEY = "pro.udeedit.devtools.pushestest.WORK_GROUP"

/** The default ID used when 'Overwrite' mode is enabled. */
const val DEFAULT_NOTIF_ID = 0

/** Fixed identifier for the group summary (header) notification. */
const val SUMMARY_ID = 9999

// SHARED PREFERENCE KEYS (Persistence)

// Behavior
const val PREF_USE_MOCK_DATA = "pref_use_mock_data"
const val PREF_OVERWRITE_NOTIFICATION = "pref_overwrite_notification"
const val PREF_IS_PERSISTENT = "pref_is_persistent"
const val PREF_MULTILINE_NOTIFICATION = "pref_multiline_notification"
const val PREF_FULL_SCREEN = "pref_full_screen"
const val PREF_GROUPED_NOTIFICATIONS = "pref_grouped_notifications"

// Timing
const val PREF_PERIODS_POS = "pref_periods_pos"
const val PREF_DELAYS_POS = "pref_delays_pos"

// Visual Style
const val PREF_USE_BIG_TEXT = "pref_use_big_text"
const val PREF_SHOW_BIG_PICTURE = "pref_show_big_picture"
const val PREF_USE_INBOX_STYLE = "pref_use_inbox_style"
const val PREF_INCLUDE_ACTIONS = "pref_include_actions"
const val PREF_SHOW_SUBTEXT = "pref_show_subtext"
const val PREF_SHOW_LARGE_ICON = "pref_show_large_icon"
const val PREF_CHRONOMETER = "pref_chronometer"

// Sensory
const val PREF_VIBRATION_ON = "pref_vibration"
const val PREF_ENABLE_SOUND = "pref_enable_sound"
//const val PREF_ENABLE_LED = "pref_enable_led"

// System config
const val PREF_IMPORTANCE_POS = "pref_importance_pos"
const val PREF_VISIBILITY_POS = "pref_visibility_pos"

// FACTORY DEFAULT VALUES

/** Default state for mock data generation. */
const val DEF_USE_MOCK_DATA = false

/** Default behavior for notification ID management. */
const val DEF_OVERWRITE_NOTIFICATION = true

const val DEF_IS_PERSISTENT = false
const val DEF_MULTILINE_NOTIFICATION = false
const val DEF_FULL_SCREEN = false
const val DEF_GROUPED_NOTIFICATIONS = false

/** Baseline state for the periodic loop (Inactive). */
const val DEF_IS_PERIODIC_ACTIVE = false

const val DEF_PERIODS_POS = 0
const val DEF_DELAYS_POS = 0
const val DEF_USE_BIG_TEXT = false
const val DEF_SHOW_BIG_PICTURE = false
const val DEF_USE_INBOX_STYLE = false
const val DEF_INCLUDE_ACTIONS = false
const val DEF_SHOW_SUBTEXT = false
const val DEF_SHOW_LARGE_ICON = false
const val DEF_CHRONOMETER = false

/** Standard sensory settings: Haptics and Audio are enabled by default. */
const val DEF_VIBRATION_ON = true
const val DEF_ENABLE_SOUND = true
//const val DEF_ENABLE_LED = false

/** Default importance set to 'High' (Index 1). */
const val DEF_IMPORTANCE_POS = 1

/** Default lock-screen visibility set to 'Public' (Index 0). */
const val DEF_VISIBILITY_POS = 0
