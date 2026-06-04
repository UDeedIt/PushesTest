package pro.udeedit.devtools.pushestest.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.utils.*

class MainViewModel : ViewModel() {

    // State for Title and Body
    private val _notificationTitle = mutableStateOf("")
    val notificationTitle: State<String> = _notificationTitle

    private val _notificationBody = mutableStateOf("")
    val notificationBody: State<String> = _notificationBody

    // State for UI behavior
    private val _isMockEnabled = mutableStateOf(CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA))
    val isMockEnabled: State<Boolean> = _isMockEnabled

    private val _isPeriodicActive = mutableStateOf(false)
    val isPeriodicActive: State<Boolean> = _isPeriodicActive

    init {
        refreshSettings()
    }


    fun onTitleChange(newValue: String) {
        if (!_isMockEnabled.value) _notificationTitle.value = newValue
    }

    fun onBodyChange(newValue: String) {
        if (!_isMockEnabled.value) _notificationBody.value = newValue
    }

    fun refreshSettings() {
        _isMockEnabled.value = CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)
        if (_isMockEnabled.value) {
            shuffleMockData()
        }
    }

    fun shuffleMockData() {
        val mock = PtMockDataUtils.getRandomMockData()
        _notificationTitle.value = mock.title
        _notificationBody.value = mock.body
    }

    fun togglePeriodic(active: Boolean) {
        _isPeriodicActive.value = active
    }
}
