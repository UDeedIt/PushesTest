package pro.udeedit.devtools.pushestest.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.databinding.LayoutSettingsBottomsheetBinding
import pro.udeedit.devtools.pushestest.utils.*

class SettingsBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "SettingsBottomSheet"
    }

    private var _binding: LayoutSettingsBottomsheetBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutSettingsBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()
        setupListeners()
    }

    private fun loadSettings() {
        binding.apply {
            // BEHAVIOR
            switchUseMockData.isChecked = CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)
            switchOverwrite.isChecked = CushyStorage.getBoolean(PREF_OVERWRITE_NOTIFICATION, DEF_OVERWRITE_NOTIFICATION)
            switchPersistent.isChecked = CushyStorage.getBoolean(PREF_IS_PERSISTENT, DEF_IS_PERSISTENT)
            switchMultilineText.isChecked = CushyStorage.getBoolean(PREF_MULTILINE_NOTIFICATION, DEF_MULTILINE_NOTIFICATION)
            switchFullScreen.isChecked = CushyStorage.getBoolean(PREF_FULL_SCREEN, DEF_FULL_SCREEN)
            switchGrouped.isChecked = CushyStorage.getBoolean(PREF_GROUPED_NOTIFICATIONS, DEF_GROUPED_NOTIFICATIONS)

            // TIMING
            spPeriods.setSelection(CushyStorage.getInt(PREF_PERIODS_POS, DEF_PERIODS_POS))
            spDelays.setSelection(CushyStorage.getInt(PREF_DELAYS_POS, DEF_DELAYS_POS))

            // VISUAL STYLE
            switchBigText.isChecked = CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)
            switchBigPicture.isChecked = CushyStorage.getBoolean(PREF_SHOW_BIG_PICTURE, DEF_SHOW_BIG_PICTURE)
            switchActionButtons.isChecked = CushyStorage.getBoolean(PREF_INCLUDE_ACTIONS, DEF_INCLUDE_ACTIONS)
            switchSubText.isChecked = CushyStorage.getBoolean(PREF_SHOW_SUBTEXT, DEF_SHOW_SUBTEXT)
            switchContentInfo.isChecked = CushyStorage.getBoolean(PREF_SHOW_CONTENT_INFO, DEF_SHOW_CONTENT_INFO)
            switchLargeIcon.isChecked = CushyStorage.getBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)
            switchChronometer.isChecked = CushyStorage.getBoolean(PREF_CHRONOMETER, DEF_CHRONOMETER)

            // SENSORY
            switchVibration.isChecked = CushyStorage.getBoolean(PREF_VIBRATION_ON_ERROR, DEF_VIBRATION_ON_ERROR)
            switchSound.isChecked = CushyStorage.getBoolean(PREF_ENABLE_SOUND, DEF_ENABLE_SOUND)
            switchLED.isChecked = CushyStorage.getBoolean(PREF_ENABLE_LED, DEF_ENABLE_LED)

            // SYSTEM CONFIG
            spImportance.setSelection(CushyStorage.getInt(PREF_IMPORTANCE_POS, DEF_IMPORTANCE_POS))
            spVisibility.setSelection(CushyStorage.getInt(PREF_VISIBILITY_POS, DEF_VISIBILITY_POS))
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnCloseSettings.setOnClickListener { dismiss() }
            btnResetSettings.setOnClickListener { resetToDefaults() }

            // BEHAVIOR
            switchUseMockData.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_USE_MOCK_DATA, b) }
            switchOverwrite.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_OVERWRITE_NOTIFICATION, b) }
            switchPersistent.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_IS_PERSISTENT, b) }
            switchMultilineText.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_MULTILINE_NOTIFICATION, b) }
            switchFullScreen.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_FULL_SCREEN, b) }
            switchGrouped.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_GROUPED_NOTIFICATIONS, b) }

            // VISUAL STYLE
            switchBigText.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_USE_BIG_TEXT, b) }
            switchBigPicture.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_SHOW_BIG_PICTURE, b) }
            switchActionButtons.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_INCLUDE_ACTIONS, b) }
            switchSubText.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_SHOW_SUBTEXT, b) }
            switchContentInfo.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_SHOW_CONTENT_INFO, b) }
            switchLargeIcon.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_SHOW_LARGE_ICON, b) }
            switchChronometer.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_CHRONOMETER, b) }

            // SENSORY
            switchVibration.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_VIBRATION_ON_ERROR, b) }
            switchSound.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_ENABLE_SOUND, b) }
            switchLED.setOnCheckedChangeListener { _, b -> CushyStorage.saveBoolean(PREF_ENABLE_LED, b) }

            // SPINNERS
            setupSpinnerListener(spPeriods, PREF_PERIODS_POS)
            setupSpinnerListener(spDelays, PREF_DELAYS_POS)
            setupSpinnerListener(spImportance, PREF_IMPORTANCE_POS)
            setupSpinnerListener(spVisibility, PREF_VISIBILITY_POS)
        }
    }

    private fun setupSpinnerListener(spinner: android.widget.Spinner, key: String) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                CushyStorage.saveInt(key, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun resetToDefaults() {
        CushyStorage.saveBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)
        CushyStorage.saveBoolean(PREF_OVERWRITE_NOTIFICATION, DEF_OVERWRITE_NOTIFICATION)
        CushyStorage.saveBoolean(PREF_IS_PERSISTENT, DEF_IS_PERSISTENT)
        CushyStorage.saveBoolean(PREF_MULTILINE_NOTIFICATION, DEF_MULTILINE_NOTIFICATION)
        CushyStorage.saveBoolean(PREF_FULL_SCREEN, DEF_FULL_SCREEN)
        CushyStorage.saveBoolean(PREF_GROUPED_NOTIFICATIONS, DEF_GROUPED_NOTIFICATIONS)
        CushyStorage.saveInt(PREF_PERIODS_POS, DEF_PERIODS_POS)
        CushyStorage.saveInt(PREF_DELAYS_POS, DEF_DELAYS_POS)
        CushyStorage.saveBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)
        CushyStorage.saveBoolean(PREF_SHOW_BIG_PICTURE, DEF_SHOW_BIG_PICTURE)
        CushyStorage.saveBoolean(PREF_INCLUDE_ACTIONS, DEF_INCLUDE_ACTIONS)
        CushyStorage.saveBoolean(PREF_SHOW_SUBTEXT, DEF_SHOW_SUBTEXT)
        CushyStorage.saveBoolean(PREF_SHOW_CONTENT_INFO, DEF_SHOW_CONTENT_INFO)
        CushyStorage.saveBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)
        CushyStorage.saveBoolean(PREF_CHRONOMETER, DEF_CHRONOMETER)
        CushyStorage.saveBoolean(PREF_VIBRATION_ON_ERROR, DEF_VIBRATION_ON_ERROR)
        CushyStorage.saveBoolean(PREF_ENABLE_SOUND, DEF_ENABLE_SOUND)
        CushyStorage.saveBoolean(PREF_ENABLE_LED, DEF_ENABLE_LED)
        CushyStorage.saveInt(PREF_IMPORTANCE_POS, DEF_IMPORTANCE_POS)
        CushyStorage.saveInt(PREF_VISIBILITY_POS, DEF_VISIBILITY_POS)

        loadSettings()

        Toast.makeText(requireContext(), R.string.settings_reset_msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as? MainActivity)?.refreshSettingsFromStorage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
