package pro.udeedit.devtools.pushestest.ui

import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import kotlin.jvm.java
import androidx.core.net.toUri

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
            switchLargeIcon.isChecked = CushyStorage.getBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)
            switchChronometer.isChecked = CushyStorage.getBoolean(PREF_CHRONOMETER, DEF_CHRONOMETER)

            // SENSORY
            switchVibration.isChecked = CushyStorage.getBoolean(PREF_VIBRATION_ON, DEF_VIBRATION_ON)
            switchSound.isChecked = CushyStorage.getBoolean(PREF_ENABLE_SOUND, DEF_ENABLE_SOUND)

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
            switchUseMockData.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_USE_MOCK_DATA,
                    b
                )
            }

            switchOverwrite.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_OVERWRITE_NOTIFICATION,
                    b
                )
            }

            switchPersistent.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_IS_PERSISTENT,
                    b
                )
            }

            switchMultilineText.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_MULTILINE_NOTIFICATION,
                    b
                )
            }

            switchGrouped.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_GROUPED_NOTIFICATIONS,
                    b
                )
            }

            //handle this with permissions
            switchFullScreen.setOnCheckedChangeListener { _, isChecked ->
                CushyStorage.saveBoolean(PREF_FULL_SCREEN, isChecked)

                if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val manager = requireContext().getSystemService(NotificationManager::class.java)
                    if (!manager.canUseFullScreenIntent()) {
                        // User hasn't allowed it yet, send them to settings
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.warn_grant_full_screen),
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT)
                        intent.data = "package:${requireContext().packageName}".toUri()
                        startActivity(intent)
                    }
                }
            }

            // VISUAL STYLE

            // Logic for Big Text
            switchBigText.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switchBigPicture.isChecked = false
                    switchInboxStyle.isChecked = false

                    CushyStorage.saveBoolean(PREF_SHOW_BIG_PICTURE, false)
                    CushyStorage.saveBoolean(PREF_USE_INBOX_STYLE, false)
                }

                CushyStorage.saveBoolean(PREF_USE_BIG_TEXT, isChecked)
            }

            // Logic for Big Picture
            switchBigPicture.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switchBigText.isChecked = false
                    switchInboxStyle.isChecked = false

                    CushyStorage.saveBoolean(PREF_USE_BIG_TEXT, false)
                    CushyStorage.saveBoolean(PREF_USE_INBOX_STYLE, false)
                }

                CushyStorage.saveBoolean(PREF_SHOW_BIG_PICTURE, isChecked)
            }

            // Logic for Inbox Style
            switchInboxStyle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    switchBigText.isChecked = false
                    switchBigPicture.isChecked = false

                    CushyStorage.saveBoolean(PREF_USE_BIG_TEXT, false)
                    CushyStorage.saveBoolean(PREF_SHOW_BIG_PICTURE, false)
                }

                CushyStorage.saveBoolean(PREF_USE_INBOX_STYLE, isChecked)
            }

            switchActionButtons.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_INCLUDE_ACTIONS,
                    b
                )
            }

            switchSubText.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_SHOW_SUBTEXT,
                    b
                )
            }

            switchLargeIcon.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_SHOW_LARGE_ICON,
                    b
                )
            }

            switchChronometer.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_CHRONOMETER,
                    b
                )
            }

            // SENSORY
            switchVibration.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_VIBRATION_ON,
                    b
                )
            }

            switchSound.setOnCheckedChangeListener { _, b ->
                CushyStorage.saveBoolean(
                    PREF_ENABLE_SOUND,
                    b
                )
            }

            // SPINNERS
            setupSpinnerListener(spPeriods, PREF_PERIODS_POS)
            setupSpinnerListener(spDelays, PREF_DELAYS_POS)
            setupSpinnerListener(spImportance, PREF_IMPORTANCE_POS)
            setupSpinnerListener(spVisibility, PREF_VISIBILITY_POS)


            binding.apply {
                // --- BEHAVIOR INFO ---
                infoUseMockData.setOnClickListener {
                    showInfoDialog(
                        tvLabelMockData,
                        R.string.info_use_mock_body
                    )
                }

                infoOverwrite.setOnClickListener {
                    showInfoDialog(
                        tvLabelOverwrite,
                        R.string.info_overwrite_body
                    )
                }

                infoPersistent.setOnClickListener {
                    showInfoDialog(
                        tvLabelPersistent,
                        R.string.info_persistent_body
                    )
                }

                infoMultiline.setOnClickListener {
                    showInfoDialog(
                        tvLabelMultiline,
                        R.string.info_multiline_body
                    )
                }

                infoFullScreen.setOnClickListener {
                    showInfoDialog(
                        tvLabelFullScreen,
                        R.string.info_full_screen_body
                    )
                }

                infoGrouped.setOnClickListener {
                    showInfoDialog(
                        tvLabelGrouped,
                        R.string.info_grouped_body
                    )
                }

                // --- TIMING INFO ---
                infoPeriods.setOnClickListener {
                    showInfoDialog(
                        tvLabelPeriods,
                        R.string.info_periods_body
                    )
                }

                infoDelays.setOnClickListener {
                    showInfoDialog(
                        tvLabelDelays,
                        R.string.info_delays_body
                    )
                }

                // --- VISUAL STYLE INFO ---
                infoBigText.setOnClickListener {
                    showInfoDialog(
                        tvLabelBigText,
                        R.string.info_big_text_body
                    )
                }

                infoBigPicture.setOnClickListener {
                    showInfoDialog(
                        tvLabelBigPicture,
                        R.string.info_big_picture_body
                    )
                }

                infoInboxStyle.setOnClickListener {
                    showInfoDialog(
                        tvLabelInbox,
                        R.string.info_inbox_body
                    )
                }

                infoActionButtons.setOnClickListener {
                    showInfoDialog(
                        tvLabelActions,
                        R.string.info_actions_body
                    )
                }

                infoSubText.setOnClickListener {
                    showInfoDialog(
                        tvLabelSubText,
                        R.string.info_subtext_body
                    )
                }

                infoLargeIcon.setOnClickListener {
                    showInfoDialog(
                        tvLabelLargeIcon,
                        R.string.info_large_icon_body
                    )
                }

                infoChronometer.setOnClickListener {
                    showInfoDialog(
                        tvLabelChronometer,
                        R.string.info_chronometer_body
                    )
                }

                // --- SENSORY INFO ---
                infoVibration.setOnClickListener {
                    showInfoDialog(
                        tvLabelVibration,
                        R.string.info_vibration_body
                    )
                }

                infoSound.setOnClickListener {
                    showInfoDialog(
                        tvLabelSound,
                        R.string.info_sound_body
                    )
                }

                // --- SYSTEM CONFIG INFO ---
                infoImportance.setOnClickListener {
                    showInfoDialog(
                        tvLabelImportance,
                        R.string.info_importance_body
                    )
                }

                infoVisibility.setOnClickListener {
                    showInfoDialog(
                        tvLabelVisibility,
                        R.string.info_visibility_body
                    )
                }
            }
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
        CushyStorage.saveBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)
        CushyStorage.saveBoolean(PREF_CHRONOMETER, DEF_CHRONOMETER)
        CushyStorage.saveBoolean(PREF_VIBRATION_ON, DEF_VIBRATION_ON)
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

    private fun showInfoDialog(labelView: android.widget.TextView, messageResId: Int) {
        // Get the raw string from resources
        var message = getString(messageResId)

        // If this is the BigText info, inject the resource integer
        if (messageResId == R.string.info_big_text_body) {
            val limit = resources.getInteger(R.integer.body_truncation_limit)
            message = getString(messageResId, limit) // Injects 40 or 60 automatically
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_info_custom, null)

        // Create the dialog
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Ensure the background is transparent so the CardView rounding is visible
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set Data
        dialogView.findViewById<android.widget.TextView>(R.id.dialogTitle).text = labelView.text
        dialogView.findViewById<android.widget.TextView>(R.id.dialogMessage).text = message

        dialogView.findViewById<View>(R.id.btnDialogClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
