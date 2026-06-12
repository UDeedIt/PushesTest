package pro.udeedit.devtools.pushestest.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.utils.*

class MainViewModel : ViewModel() {

    // --- ENCAPSULATED STATE ---
    private val _state = mutableStateOf(loadInitialState())
    val state: State<SettingsState> = _state

    /** Loads all values from CushyStorage into the initial state object */
    private fun loadInitialState() = SettingsState(
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
    ).also {
        // Initial sync of mock content if enabled
        if (it.isMockEnabled) syncMockContent(it)
    }

    // --- LOGIC FUNCTIONS ---

    fun onTitleChange(v: String) { if (!_state.value.isMockEnabled) _state.value = _state.value.copy(notificationTitle = v) }
    fun onBodyChange(v: String) { if (!_state.value.isMockEnabled) _state.value = _state.value.copy(notificationBody = v) }

    /** Handles all Boolean switches via a unified Enum entry point */
    fun set(setting: AppSetting, value: Boolean) {
        when (setting) {
            AppSetting.MOCK_DATA -> {
                saveBool(PREF_USE_MOCK_DATA, value)
                _state.value = _state.value.copy(isMockEnabled = value)
                syncMockContent(_state.value)
            }
            AppSetting.BIG_TEXT -> {
                if (value) clearVisualStyles()
                saveBool(PREF_USE_BIG_TEXT, value)
                _state.value = _state.value.copy(useBigText = value)
            }
            AppSetting.BIG_PICTURE -> {
                if (value) clearVisualStyles()
                saveBool(PREF_SHOW_BIG_PICTURE, value)
                _state.value = _state.value.copy(showBigPicture = value)
            }
            AppSetting.INBOX_STYLE -> {
                if (value) clearVisualStyles()
                saveBool(PREF_USE_INBOX_STYLE, value)
                _state.value = _state.value.copy(useInboxStyle = value)
            }
            // Mappings for other simple booleans...
            AppSetting.OVERWRITE -> { saveBool(PREF_OVERWRITE_NOTIFICATION, value); _state.value = _state.value.copy(isOverwrite = value) }
            AppSetting.PERSISTENT -> { saveBool(PREF_IS_PERSISTENT, value); _state.value = _state.value.copy(isPersistent = value) }
            AppSetting.MULTILINE -> { saveBool(PREF_MULTILINE_NOTIFICATION, value); _state.value = _state.value.copy(isMultiline = value) }
            AppSetting.FULL_SCREEN -> { saveBool(PREF_FULL_SCREEN, value); _state.value = _state.value.copy(isFullScreen = value) }
            AppSetting.GROUPED -> { saveBool(PREF_GROUPED_NOTIFICATIONS, value); _state.value = _state.value.copy(isGrouped = value) }
            AppSetting.ACTIONS -> { saveBool(PREF_INCLUDE_ACTIONS, value); _state.value = _state.value.copy(includeActions = value) }
            AppSetting.SUBTEXT -> { saveBool(PREF_SHOW_SUBTEXT, value); _state.value = _state.value.copy(showSubtext = value) }
            AppSetting.LARGE_ICON -> { saveBool(PREF_SHOW_LARGE_ICON, value); _state.value = _state.value.copy(showLargeIcon = value) }
            AppSetting.CHRONOMETER -> { saveBool(PREF_CHRONOMETER, value); _state.value = _state.value.copy(useChronometer = value) }
            AppSetting.VIBRATION -> { saveBool(PREF_VIBRATION_ON, value); _state.value = _state.value.copy(vibrationOn = value) }
            AppSetting.SOUND -> { saveBool(PREF_ENABLE_SOUND, value); _state.value = _state.value.copy(enableSound = value) }
            else -> {}
        }
    }

    /** Handles all Dropdown integer selections */
    fun set(setting: AppSetting, value: Int) {
        when (setting) {
            AppSetting.PERIODS -> { saveInt(PREF_PERIODS_POS, value); _state.value = _state.value.copy(periodsPos = value) }
            AppSetting.DELAYS -> { saveInt(PREF_DELAYS_POS, value); _state.value = _state.value.copy(delaysPos = value) }
            AppSetting.IMPORTANCE -> { saveInt(PREF_IMPORTANCE_POS, value); _state.value = _state.value.copy(importancePos = value) }
            AppSetting.VISIBILITY -> { saveInt(PREF_VISIBILITY_POS, value); _state.value = _state.value.copy(visibilityPos = value) }
            else -> {}
        }
    }

    fun togglePeriodic(active: Boolean) {
        _state.value = _state.value.copy(isPeriodicActive = active)
    }

    fun shuffleMockData() {
        val s = _state.value
        val isLarge = s.isMultiline || s.useBigText
        val mock = if (isLarge) PtMockDataUtils.getRandomBigMockData() else PtMockDataUtils.getRandomMockData()
        _state.value = _state.value.copy(notificationTitle = mock.title, notificationBody = mock.body)
    }

    private fun syncMockContent(s: SettingsState) {
        if (s.isMockEnabled) shuffleMockData() else _state.value = _state.value.copy(notificationTitle = "", notificationBody = "")
    }


    private fun clearVisualStyles() {
        saveBool(PREF_USE_BIG_TEXT, false); saveBool(PREF_SHOW_BIG_PICTURE, false); saveBool(PREF_USE_INBOX_STYLE, false)
        _state.value = _state.value.copy(useBigText = false, showBigPicture = false, useInboxStyle = false)
    }

    fun resetToDefaults() {
        // 1. Physically reset every key in CushyStorage using your DEF_ constants
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

        saveInt(PREF_IMPORTANCE_POS, DEF_IMPORTANCE_POS)
        saveInt(PREF_VISIBILITY_POS, DEF_VISIBILITY_POS)

        // 2. Atomically update the UI State
        // Since SettingsState defaults match our DEF_ constants, we just create a new one.
        _state.value = SettingsState(
            notificationTitle = "",
            notificationBody = ""
        )

        // 3. Optional: If mock data is the default, sync it
        if (DEF_USE_MOCK_DATA) shuffleMockData()
    }


    // Storage Helpers
    private fun saveBool(key: String, v: Boolean) = CushyStorage.saveBoolean(key, v)
    private fun saveInt(key: String, v: Int) = CushyStorage.saveInt(key, v)
}
