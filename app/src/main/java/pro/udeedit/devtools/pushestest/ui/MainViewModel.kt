package pro.udeedit.devtools.pushestest.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import pro.udeedit.devtools.anarchist.AnarchistPermissionStatus
import pro.udeedit.devtools.anarchist.AnarchistPermissionUtils
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.domain.usecase.PublishNotificationUseCase
import pro.udeedit.devtools.pushestest.utils.*
import javax.inject.Inject

/**
 * ViewModel for the Pushes Test application.
 *
 * This class serves as the central hub for the MVI (Model-View-Intent) architecture.
 * It maintains the [SettingsState], handles persistence via [CushyStorage],
 * and orchestrates notification delivery and haptic feedback.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val publishNotificationUseCase: PublishNotificationUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var periodicRunnable: Runnable

    // MVI STATE ENCAPSULATION

    private val _state = mutableStateOf(loadInitialState())

    /**
     * The single source of truth for the Compose UI.
     * Observe this state to react to any preference or input changes.
     */
    val state: State<SettingsState> = _state

    // HAPTIC CONFIGURATION

    private val patternConfirmation = longArrayOf(0, 100, 50, 100)
    private val patternError = longArrayOf(0, 300, 100, 300, 100, 300)
    private val vibrationDurationClick = 60L


    init {
        // Prepare system notification channels on app startup
        publishNotificationUseCase.initializeChannels()

        // Sync UI fields with mock data if preference is enabled
        if (_state.value.isMockEnabled) {
            shuffleMockData()
        }
    }


    /**
     * Loads current user preferences from persistent storage to initialize the MVI state.
     */
    private fun loadInitialState(): SettingsState {

        return SettingsState(
            isMockEnabled = CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA),
            isOverwrite = CushyStorage.getBoolean(PREF_OVERWRITE_NOTIFICATION, DEF_OVERWRITE_NOTIFICATION),
            isPersistent = CushyStorage.getBoolean(PREF_IS_PERSISTENT, DEF_IS_PERSISTENT),
            isMultiline = CushyStorage.getBoolean(PREF_MULTILINE_NOTIFICATION, DEF_MULTILINE_NOTIFICATION),
            isFullScreen = CushyStorage.getBoolean(PREF_FULL_SCREEN, DEF_FULL_SCREEN),
            isGrouped = CushyStorage.getBoolean(PREF_GROUPED_NOTIFICATIONS, DEF_GROUPED_NOTIFICATIONS),
            periodsPos = CushyStorage.getInt(PREF_PERIODS_POS, DEF_PERIODS_POS),
            delaysPos = CushyStorage.getInt(PREF_DELAYS_POS, DEF_DELAYS_POS),
            useBigText = CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT),
            showBigPicture = CushyStorage.getBoolean(PREF_SHOW_BIG_PICTURE, DEF_SHOW_BIG_PICTURE),
            useInboxStyle = CushyStorage.getBoolean(PREF_USE_INBOX_STYLE, DEF_USE_INBOX_STYLE),
            includeActions = CushyStorage.getBoolean(PREF_INCLUDE_ACTIONS, DEF_INCLUDE_ACTIONS),
            showSubtext = CushyStorage.getBoolean(PREF_SHOW_SUBTEXT, DEF_SHOW_SUBTEXT),
            showLargeIcon = CushyStorage.getBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON),
            useChronometer = CushyStorage.getBoolean(PREF_CHRONOMETER, DEF_CHRONOMETER),
            vibrationOn = CushyStorage.getBoolean(PREF_VIBRATION_ON, DEF_VIBRATION_ON),
            enableSound = CushyStorage.getBoolean(PREF_ENABLE_SOUND, DEF_ENABLE_SOUND),
            importancePos = CushyStorage.getInt(PREF_IMPORTANCE_POS, DEF_IMPORTANCE_POS),
            visibilityPos = CushyStorage.getInt(PREF_VISIBILITY_POS, DEF_VISIBILITY_POS)
        )
    }

    // MANUAL INPUT HANDLERS

    /**
     * Updates the title state unless mock mode is active.
     */
    fun onTitleChange(newValue: String) {
        if (!_state.value.isMockEnabled) {
            _state.value = _state.value.copy(
                notificationTitle = newValue
            )
        }
    }

    /**
     * Updates the body state unless mock mode is active.
     */
    fun onBodyChange(newValue: String) {
        if (!_state.value.isMockEnabled) {
            _state.value = _state.value.copy(
                notificationBody = newValue
            )
        }
    }

    // MVI INTENT HANDLERS (UNIFIED SET FUNCTIONS)

    /**
     * Processes all Boolean setting changes.
     * Handles side-effects like mutual exclusion and mock data synchronization.
     */
    fun set(setting: AppSetting, value: Boolean) {
        when (setting) {
            AppSetting.MOCK_DATA -> {
                toggleMockData(value)
            }
            AppSetting.BIG_TEXT -> {
                toggleBigText(value)
            }
            AppSetting.BIG_PICTURE -> {
                toggleBigPicture(value)
            }
            AppSetting.INBOX_STYLE -> {
                toggleInboxStyle(value)
            }
            AppSetting.OVERWRITE -> {
                toggleOverwrite(value)
            }
            AppSetting.FULL_SCREEN -> {
                toggleFullScreen(value)
            }
            AppSetting.PERSISTENT -> {
                togglePersistent(value)
            }
            AppSetting.MULTILINE -> {
                toggleMultiline(value)
            }
            AppSetting.GROUPED -> {
                toggleGrouped(value)
            }
            AppSetting.ACTIONS -> {
                toggleActions(value)
            }
            AppSetting.SUBTEXT -> {
                toggleSubtext(value)
            }
            AppSetting.LARGE_ICON -> {
                toggleLargeIcon(value)
            }
            AppSetting.CHRONOMETER -> {
                toggleChronometer(value)
            }
            AppSetting.VIBRATION -> {
                toggleVibration(value)
            }
            AppSetting.SOUND -> {
                toggleSound(value)
            }

            else -> {
                // No mapping for these types
            }
        }
    }

    /**
     * Processes all Integer (Spinner) preference updates and persists them to storage.
     */
    fun set(setting: AppSetting, value: Int) {
        when (setting) {
            AppSetting.PERIODS -> {
                updatePeriods(value)
            }
            AppSetting.DELAYS -> {
                updateDelays(value)
            }
            AppSetting.IMPORTANCE -> {
                updateImportance(value)
            }
            AppSetting.VISIBILITY -> {
                updateVisibility(value)
            }

            else -> {
                // No mapping for these types
            }
        }
    }

    // EXPLICIT TOGGLE LOGIC (BUSINESS RULES)

    private fun toggleMockData(active: Boolean) {
        saveBool(PREF_USE_MOCK_DATA, active)
        _state.value = _state.value.copy(
            isMockEnabled = active
        )

        if (active) {
            shuffleMockData()

        } else {
            _state.value = _state.value.copy(
                notificationTitle = "",
                notificationBody = ""
            )
        }
    }

    private fun toggleOverwrite(active: Boolean) {
        if (active) {
            if (_state.value.isFullScreen) {
                toggleFullScreen(false)
            }
        }

        saveBool(PREF_OVERWRITE_NOTIFICATION, active)

        _state.value = _state.value.copy(
            isOverwrite = active
        )
    }

    private fun toggleFullScreen(active: Boolean) {
        if (active) {
            if (_state.value.isOverwrite) {
                toggleOverwrite(false)
            }
        }

        saveBool(PREF_FULL_SCREEN, active)

        _state.value = _state.value.copy(
            isFullScreen = active
        )
    }

    private fun toggleBigText(active: Boolean) {
        if (active) {
            clearVisualStyles()
        }

        saveBool(PREF_USE_BIG_TEXT, active)

        _state.value = _state.value.copy(
            useBigText = active
        )
    }

    private fun toggleBigPicture(active: Boolean) {
        if (active) {
            clearVisualStyles()
        }

        saveBool(PREF_SHOW_BIG_PICTURE, active)

        _state.value = _state.value.copy(
            showBigPicture = active
        )
    }

    private fun toggleInboxStyle(active: Boolean) {
        if (active) {
            clearVisualStyles()
        }
        saveBool(PREF_USE_INBOX_STYLE, active)
        _state.value = _state.value.copy(
            useInboxStyle = active
        )
    }

    private fun togglePersistent(v: Boolean) {
        saveBool(PREF_IS_PERSISTENT, v)

        _state.value = _state.value.copy(
            isPersistent = v
        )
    }

    private fun toggleMultiline(v: Boolean) {
        saveBool(PREF_MULTILINE_NOTIFICATION, v)

        _state.value = _state.value.copy(
            isMultiline = v
        )
    }

    private fun toggleGrouped(v: Boolean) {
        saveBool(PREF_GROUPED_NOTIFICATIONS, v)

        _state.value = _state.value.copy(
            isGrouped = v
        )
    }

    private fun toggleActions(v: Boolean) {
        saveBool(PREF_INCLUDE_ACTIONS, v)

        _state.value = _state.value.copy(
            includeActions = v
        )
    }

    private fun toggleSubtext(v: Boolean) {
        saveBool(PREF_SHOW_SUBTEXT, v)

        _state.value = _state.value.copy(
            showSubtext = v
        )
    }

    private fun toggleLargeIcon(v: Boolean) {
        saveBool(PREF_SHOW_LARGE_ICON, v)

        _state.value = _state.value.copy(
            showLargeIcon = v
        )
    }

    private fun toggleChronometer(v: Boolean) {
        saveBool(PREF_CHRONOMETER, v)

        _state.value = _state.value.copy(
            useChronometer = v
        )
    }

    private fun toggleVibration(v: Boolean) {
        saveBool(PREF_VIBRATION_ON, v)
        _state.value = _state.value.copy(
            vibrationOn = v
        )
    }

    private fun toggleSound(v: Boolean) {
        saveBool(PREF_ENABLE_SOUND, v)

        _state.value = _state.value.copy(
            enableSound = v
        )
    }

    private fun updatePeriods(v: Int) {
        saveInt(PREF_PERIODS_POS, v)

        _state.value = _state.value.copy(
            periodsPos = v
        )
    }

    private fun updateDelays(v: Int) {
        saveInt(PREF_DELAYS_POS, v)
        _state.value = _state.value.copy(
            delaysPos = v
        )
    }

    private fun updateImportance(v: Int) {
        saveInt(PREF_IMPORTANCE_POS, v)

        _state.value = _state.value.copy(
            importancePos = v
        )
    }

    private fun updateVisibility(v: Int) {
        saveInt(PREF_VISIBILITY_POS, v)

        _state.value = _state.value.copy(
            visibilityPos = v
        )
    }

    // HELPER LOGIC

    /**
     * Resets mutually exclusive visual styles to ensure
     * only one [androidx.core.app.NotificationCompat.Style] is active at a time.
     */
    private fun clearVisualStyles() {
        saveBool(PREF_USE_BIG_TEXT, false)
        saveBool(PREF_SHOW_BIG_PICTURE, false)
        saveBool(PREF_USE_INBOX_STYLE, false)

        _state.value = _state.value.copy(
            useBigText = false,
            showBigPicture = false,
            useInboxStyle = false
        )
    }

    /**
     * Fetches a new random notification from the mock dataset
     * and updates the observable UI state.
     */
    fun shuffleMockData() {
        val s = _state.value
        val isLarge = s.isMultiline || s.useBigText
        val mock = if (isLarge) {
            PtMockDataUtils.getRandomBigMockData()
        } else {
            PtMockDataUtils.getRandomMockData()
        }

        _state.value = _state.value.copy(
            notificationTitle = mock.title,
            notificationBody = mock.body
        )
    }

    /**
     * Restores all application settings to their original factory defaults.
     *
     * This process ensures a clean state by:
     * 1. Overwriting all keys in [CushyStorage] with [DEF_] constants.
     * 2. Emitting a fresh [SettingsState] object to the UI layer.
     */
    fun resetToDefaults() {
        // Reset Persistent Storage
        saveBool(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)
        saveBool(PREF_OVERWRITE_NOTIFICATION, DEF_OVERWRITE_NOTIFICATION)
        saveBool(PREF_IS_PERSISTENT, DEF_IS_PERSISTENT)
        saveBool(PREF_MULTILINE_NOTIFICATION, DEF_MULTILINE_NOTIFICATION)
        saveBool(PREF_FULL_SCREEN, DEF_FULL_SCREEN)
        saveBool(PREF_GROUPED_NOTIFICATIONS, DEF_GROUPED_NOTIFICATIONS)

        saveInt(PREF_PERIODS_POS, DEF_PERIODS_POS)
        saveInt(PREF_DELAYS_POS, DEF_DELAYS_POS)

        saveBool(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)
        saveBool(PREF_SHOW_BIG_PICTURE, DEF_SHOW_BIG_PICTURE)
        saveBool(PREF_USE_INBOX_STYLE, DEF_USE_INBOX_STYLE)
        saveBool(PREF_INCLUDE_ACTIONS, DEF_INCLUDE_ACTIONS)
        saveBool(PREF_SHOW_SUBTEXT, DEF_SHOW_SUBTEXT)
        saveBool(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)
        saveBool(PREF_CHRONOMETER, DEF_CHRONOMETER)

        saveBool(PREF_VIBRATION_ON, DEF_VIBRATION_ON)
        saveBool(PREF_ENABLE_SOUND, DEF_ENABLE_SOUND)
//        saveBool(PREF_ENABLE_LED, DEF_ENABLE_LED)

        saveInt(PREF_IMPORTANCE_POS, DEF_IMPORTANCE_POS)
        saveInt(PREF_VISIBILITY_POS, DEF_VISIBILITY_POS)

        // Reset UI State Object
        // We instantiate a new SettingsState which uses the DEF_ constants
        // as its internal default values.
        _state.value = SettingsState(
            notificationTitle = "",
            notificationBody = "",
            isMockEnabled = DEF_USE_MOCK_DATA,
            isOverwrite = DEF_OVERWRITE_NOTIFICATION,
            isPersistent = DEF_IS_PERSISTENT,
            isMultiline = DEF_MULTILINE_NOTIFICATION,
            isFullScreen = DEF_FULL_SCREEN,
            isGrouped = DEF_GROUPED_NOTIFICATIONS,
            isPeriodicActive = DEF_IS_PERIODIC_ACTIVE,
            periodsPos = DEF_PERIODS_POS,
            delaysPos = DEF_DELAYS_POS,
            useBigText = DEF_USE_BIG_TEXT,
            showBigPicture = DEF_SHOW_BIG_PICTURE,
            useInboxStyle = DEF_USE_INBOX_STYLE,
            includeActions = DEF_INCLUDE_ACTIONS,
            showSubtext = DEF_SHOW_SUBTEXT,
            showLargeIcon = DEF_SHOW_LARGE_ICON,
            useChronometer = DEF_CHRONOMETER,
            vibrationOn = DEF_VIBRATION_ON,
            enableSound = DEF_ENABLE_SOUND,
            importancePos = DEF_IMPORTANCE_POS,
            visibilityPos = DEF_VISIBILITY_POS
        )

        // Content Synchronization
        if (DEF_USE_MOCK_DATA) {
            shuffleMockData()
        }
    }


    // NOTIFICATION ORCHESTRATION

    /**
     * Evaluates delay and period settings to begin the notification dispatch sequence.
     */
    fun startNotificationProcess(activity: Activity) {
        val delayValues = activity.resources.getIntArray(R.array.delays_values_array)
        val delayMs = delayValues.getOrElse(_state.value.delaysPos) { 0 }.toLong()

        val periodValues = activity.resources.getIntArray(R.array.periods_values_array)
        val periodMs = periodValues.getOrElse(_state.value.periodsPos) { 0 }.toLong()

        val startAction = {
            if (periodMs > 0) {
                startPeriodicNotifications(periodMs)
            } else {
                triggerNotification()
            }
        }

        if (delayMs > 0) {
            val seconds = (delayMs / 1000).toInt()
            android.widget.Toast.makeText(
                activity,
                activity.getString(R.string.snack_scheduled, seconds),
                android.widget.Toast.LENGTH_SHORT
            ).show()

            handler.postDelayed(startAction, delayMs)

        } else {
            startAction()
        }
    }

    private fun startPeriodicNotifications(interval: Long) {
        _state.value = _state.value.copy(
            isPeriodicActive = true
        )

        periodicRunnable = object : Runnable {
            override fun run() {
                if (!_state.value.isPeriodicActive) {
                    return
                }
                if (_state.value.isMockEnabled) {
                    shuffleMockData()
                }
                triggerNotification()
                handler.postDelayed(this, interval)
            }
        }

        handler.post(periodicRunnable)
    }

    /**
     * Terminates any active periodic notification loops.
     */
    fun stopPeriodicNotifications() {
        _state.value = _state.value.copy(
            isPeriodicActive = false
        )

        if (::periodicRunnable.isInitialized) {
            handler.removeCallbacks(periodicRunnable)
        }
    }

    /**
     * Direct trigger for notification publishing.
     * Performs a final validation check and handles success/error haptics.
     */
    fun triggerNotification() {
        if (_state.value.isMockEnabled) {
            shuffleMockData()
        }

        val s = _state.value
        if (s.notificationTitle.isNotBlank() && s.notificationBody.isNotBlank()) {
            publishNotificationUseCase(s)
            vibrateSuccess()
        } else {
            vibrateError()
        }
    }

    // SENSORY HELPERS

    private fun performVibration(pattern: LongArray?, duration: Long) {
        if (!_state.value.vibrationOn) {
            return
        }

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator

        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (pattern != null) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            }

        } else {
            @Suppress("DEPRECATION")
            if (pattern != null) {
                vibrator.vibrate(pattern, -1)
            } else {
                vibrator.vibrate(duration)
            }
        }
    }

    /** Triggers a standard haptic pulse for button presses. */
    fun vibrateButtonClick() {
        performVibration(null, vibrationDurationClick)
    }

    /** Triggers a double-pulse success haptic pattern. */
    fun vibrateSuccess() {
        performVibration(patternConfirmation, 500L)
    }

    /** Triggers an aggressive error haptic pattern. */
    fun vibrateError() {
        performVibration(patternError, 1000L)
    }

    // Storage access shortcuts
    private fun saveBool(k: String, v: Boolean) {
        CushyStorage.saveBoolean(k, v)
    }

    private fun saveInt(k: String, v: Int) {
        CushyStorage.saveInt(k, v)
    }


    /**
     * Determines the set of system permissions required for notification delivery
     * based on the current Android OS version.
     *
     * @return A list containing [Manifest.permission.POST_NOTIFICATIONS] for API 33+,
     *         otherwise an empty list.
     */
    fun getRequiredPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            emptyList()
        }
    }

    /**
     * Primary entry point for the 'Send' action initiated from the UI.
     *
     * Logic Flow:
     * 1. Triggers immediate haptic feedback for the button press.
     * 2. Prepares a [MutableList] of permissions for the "anarchist" library.
     * 3. Executes the permission check and request sequence.
     * 4. If allowed, proceeds to the notification timing and delivery logic.
     *
     * @param activity The host Activity context required by the "anarchist" library
     *                 to display system permission dialogs.
     */
    fun onSendClick(activity: Activity) {
        // Trigger tactile feedback
        vibrateButtonClick()

        // Prepare the permission list as a MutableList to satisfy library signature
        val permissions: MutableList<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            mutableListOf()
        }

        // Delegate request to the internal permission module
        val result = AnarchistPermissionUtils.checkAndRequestPermissions(
            activity,
            permissions,
            REQUEST_PERMISSION_CODE
        )

        // Evaluate the final status returned by the library
        if (result.finalStatus == AnarchistPermissionStatus.ALLOWED) {
            startNotificationProcess(activity)

        } else if (result.finalStatus == AnarchistPermissionStatus.DENIED_PERMANENTLY) {
            // Redirect user to system settings if permission is blocked
            AnarchistPermissionUtils.askUserToRequestPermissionExplicitly(activity)
        }
    }


    /**
     * Internal logic to toggle the periodic active state.
     * This drives the animation between Send and Stop buttons in the UI.
     * Needed for tests
     */
    fun togglePeriodic(active: Boolean) {
        _state.value = _state.value.copy(
            isPeriodicActive = active
        )
    }

}
