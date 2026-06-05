package pro.udeedit.devtools.pushestest

import android.app.Application
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.ui.MainViewModel

/**
 * Unit tests for [MainViewModel] ensuring business logic remains stable
 * during the migration to Jetpack Compose.
 */
class MainViewModelTest {

    private lateinit var mockApplication: Application
    private lateinit var mockPrefs: SharedPreferences

    /**
     * Prepares the testing environment before each test case.
     *
     * Process:
     * 1. Mocks the [SharedPreferences] to simulate Android storage.
     * 2. Mocks the [Application] class to satisfy CushyStorage requirements.
     * 3. Stubs the 'getSharedPreferences' call to return our mock instead of null.
     * 4. Initializes [CushyStorage] with the mocked environment.
     */
    @Before
    fun setup() {
        // 1. Create the mocks
        mockPrefs = mock(SharedPreferences::class.java)
        mockApplication = mock(Application::class.java)

        // 2. Setup the "Bridge": When the app asks for prefs, give it the mock
        `when`(mockApplication.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(mockPrefs)

        // 3. Initialize the library with the correct 'Application' type
        CushyStorage.init(mockApplication)
    }

    /**
     * Test: Check if toggling periodic mode updates the state.
     */
    @Test
    fun periodicState_shouldUpdate_whenToggled() {
        // Arrange
        val viewModel = MainViewModel()

        // Act
        viewModel.togglePeriodic(true)

        // Assert
        assert(viewModel.isPeriodicActive.value)
    }

    /**
     * Verifies that updating the notification title state works correctly
     * when mock mode is disabled.
     */
    @Test
    fun titleState_shouldUpdate_whenChangedManually() {
        // Arrange
        val viewModel = MainViewModel()
        val testTitle = "New Alert"

        // Act
        viewModel.onTitleChange(testTitle)

        // Assert
        assert(viewModel.notificationTitle.value == testTitle)
    }
}
