package pro.udeedit.devtools.pushestest

import android.app.Application
import android.content.SharedPreferences
import android.os.Looper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.domain.usecase.PublishNotificationUseCase
import pro.udeedit.devtools.pushestest.ui.MainViewModel
import pro.udeedit.devtools.pushestest.utils.AppSetting

/**
 * Unit tests for [MainViewModel] ensuring business logic, MVI state transitions,
 * and smart setting constraints remain stable across updates.
 */
class MainViewModelTest {

    private lateinit var mockApplication: Application
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockUseCase: PublishNotificationUseCase
    private lateinit var viewModel: MainViewModel
    private lateinit var mockedLooper: MockedStatic<Looper>

    /**
     * Prepares the testing environment before each test case.
     *
     * * Mocks [android.content.SharedPreferences] and its [android.content.SharedPreferences.Editor]
     *    to prevent NPEs (NullPointerException) during persistence calls.
     * * Mocks Android [android.content.res.Resources] to provide valid data for timing and
     *    importance arrays.
     * * Stubs a static [Looper] to simulate the Android Main Thread on the JVM.
     * * Initializes [CushyStorage] and the [MainViewModel].
     */
    @Before
    fun setup() {
        // Mock Storage Chain
        mockPrefs = mock(SharedPreferences::class.java)
        val mockEditor = mock(SharedPreferences.Editor::class.java)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor)
        `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)

        // Mock Application and Resources
        mockApplication = mock(Application::class.java)

        // Ensure applicationContext returns the mock itself
        `when`(mockApplication.applicationContext).thenReturn(mockApplication)
        // Ensure getPackageName returns a valid string (used for SharedPreferences naming)
        `when`(mockApplication.packageName).thenReturn("pro.udeedit.devtools.pushestest")

        val mockResources = mock(android.content.res.Resources::class.java)
        `when`(mockApplication.resources).thenReturn(mockResources)
        `when`(mockResources.getIntArray(anyInt())).thenReturn(intArrayOf(0, 10000, 30000, 60000))
        `when`(mockApplication.getString(anyInt())).thenReturn("Mock String")
        `when`(mockApplication.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)

        // Initialize Libraries
        CushyStorage.init(mockApplication)
        mockUseCase = mock(PublishNotificationUseCase::class.java)

        // Mock System Looper for Handler initialization
        mockedLooper = mockStatic(Looper::class.java)
        val mainLooper = mock(Looper::class.java)
        `when`(Looper.getMainLooper()).thenReturn(mainLooper)

        viewModel = MainViewModel(mockUseCase, mockApplication)
    }

    /**
     * Releases static mocks after each test to prevent thread-leakage.
     */
    @After
    fun tearDown() {
        mockedLooper.close()
    }

    // STATE & INPUT VALIDATION

    @Test
    fun periodicState_shouldUpdate_whenToggled() {
        // Act
        viewModel.togglePeriodic(true)

        // Assert
        assert(viewModel.state.value.isPeriodicActive)
    }

    @Test
    fun titleState_shouldUpdate_whenChangedManually() {
        // Arrange
        viewModel.set(AppSetting.MOCK_DATA, false)
        val testTitle = "Manual Title"

        // Act
        viewModel.onTitleChange(testTitle)

        // Assert
        assert(viewModel.state.value.notificationTitle == testTitle)
    }

    @Test
    fun titleChange_shouldBeIgnored_whenMockModeIsActive() {
        // Arrange
        viewModel.set(AppSetting.MOCK_DATA, true)
        val initialTitle = viewModel.state.value.notificationTitle

        // Act
        viewModel.onTitleChange("Attempted manual edit")

        // Assert: Title should remain as the auto-generated mock
        assert(viewModel.state.value.notificationTitle == initialTitle)
    }

    // SMART LOGIC & CONSTRAINTS

    @Test
    fun fullScreenIntent_shouldDisableOverwrite_whenEnabled() {
        // Arrange: Start with Overwrite active
        viewModel.set(AppSetting.OVERWRITE, true)

        // Act: Enable FSI
        viewModel.set(AppSetting.FULL_SCREEN, true)

        // Assert: Overwrite must be forced OFF for FSI compatibility
        assert(!viewModel.state.value.isOverwrite)
    }

    @Test
    fun overwriteToggle_shouldDisableFullScreen_whenEnabled() {
        // Arrange: Start with FSI active
        viewModel.set(AppSetting.FULL_SCREEN, true)

        // Act: Manually enable Overwrite
        viewModel.set(AppSetting.OVERWRITE, true)

        // Assert: FSI must be forced OFF
        assert(!viewModel.state.value.isFullScreen)
    }

    @Test
    fun bigPictureToggle_shouldDisableOtherStyles_whenEnabled() {
        // Arrange: Start with Big Text active
        viewModel.set(AppSetting.BIG_TEXT, true)

        // Act: Switch to Big Picture
        viewModel.set(AppSetting.BIG_PICTURE, true)

        // Assert: Only Big Picture remains true (Mutual Exclusion)
        val state = viewModel.state.value
        assert(state.showBigPicture)
        assert(!state.useBigText)
        assert(!state.useInboxStyle)
    }

    // FUNCTIONAL FEATURES

    @Test
    fun mockDataToggle_shouldPopulateFieldsAutomatically() {
        // Act
        viewModel.set(AppSetting.MOCK_DATA, true)

        // Assert
        val state = viewModel.state.value
        assert(state.isMockEnabled)
        assert(state.notificationTitle.isNotEmpty())
        assert(state.notificationBody.isNotEmpty())
    }

    @Test
    fun resetToDefaults_shouldRestoreAllSystemSettings() {
        // Arrange: Change multiple settings away from defaults
        viewModel.set(AppSetting.VIBRATION, false)
        viewModel.set(AppSetting.MOCK_DATA, true)
        viewModel.set(AppSetting.IMPORTANCE, 3)

        // Act
        viewModel.resetToDefaults()

        // Assert: Check against pt_constants DEF_ values
        val state = viewModel.state.value
        assert(state.vibrationOn)
        assert(!state.isMockEnabled)
        assert(state.importancePos == 1)
    }
}
