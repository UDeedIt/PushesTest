package pro.udeedit.devtools.pushestest.ui.screens

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.MainViewModel
import pro.udeedit.devtools.pushestest.ui.SettingsState
import pro.udeedit.devtools.pushestest.ui.components.CategoryHeader
import pro.udeedit.devtools.pushestest.ui.components.PtSettingsDropdown
import pro.udeedit.devtools.pushestest.ui.components.SettingsHeader
import pro.udeedit.devtools.pushestest.ui.components.SettingsRow
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme
import pro.udeedit.devtools.pushestest.utils.AppSetting
import androidx.core.net.toUri

/**
 * Stateful wrapper for the Settings screen.
 * Encapsulated ViewModel pattern
 * Handles the logic for:
 * - Persisting settings via [MainViewModel].
 * - Requesting sensitive system permissions (FSI, Battery).
 * - Directing users to OEM-specific settings pages for background execution.
 */
@Composable
fun SettingsBottomSheet(
    viewModel: MainViewModel,
    onInfoClick: (Int, Int) -> Unit,
    onClose: () -> Unit
) {
    // Collect the single state object
    val state by viewModel.state
    val context = LocalContext.current
    val overwriteWarning = stringResource(R.string.warn_overwrite_disabled)

    // Handles both the state update and the permission side-effect
    // Unified Action Handler (MVI Intent Logic)
    val handleSettingChange: (AppSetting, Any) -> Unit = { setting, value ->
        // Update the ViewModel State ---
        if (value is Boolean) {
            viewModel.set(setting, value)

        } else if (value is Int) {
            viewModel.set(setting, value)
        }

        // Handle Side Effects (Triggered only when turned ON) ---
        if (value == true) {
            when (setting) {
                AppSetting.FULL_SCREEN -> {
                    // THE SMART LOGIC: If FSI is ON, Overwrite MUST be OFF
                    if (state.isOverwrite) {
                        viewModel.set(AppSetting.OVERWRITE, false)
                        @SuppressLint("ContextCast")
                        Toast.makeText(context, overwriteWarning, Toast.LENGTH_LONG).show()
                    }

                    // PERMISSION CHECK: See if we already have the right to show FSI
                    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    val isFsiAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        manager?.canUseFullScreenIntent() ?: true
                    } else true

                    // SMART REDIRECTION: Only open system pages if permission is MISSING
                    if (!isFsiAllowed) {
                        checkAndRequestFullScreenIntent(context)
                        openOtherPermissionsSettings(context)
                        requestIgnoreBatteryOptimizations(context)
                    }
                }

                else -> { /* No side effects for other settings */ }
            }
        }
    }



    SettingsBottomSheetContent(
        state = state,
        onSettingChange = handleSettingChange,
        onInfoClick = onInfoClick,
        onClose = onClose,
        onReset = { viewModel.resetToDefaults() },
        modifier = Modifier
    )
}

/**
 * The stateless UI representation of the Settings screen.
 * Organizes 20+ notification configurations into logical categories.
 *
 * @param state The current snapshot of all user preferences.
 * @param onSettingChange Callback triggered when a switch or dropdown is modified.
 * @param onInfoClick Callback to display the educational documentation for a setting.
 */
@Composable
fun SettingsBottomSheetContent(
    state: SettingsState,
    onSettingChange: (AppSetting, Any) -> Unit,
    onInfoClick: (Int, Int) -> Unit,
    onClose: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal_middle))
    ) {
        SettingsHeader(
            onReset = onReset,
            onClose = onClose,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // CATEGORY: BEHAVIOR
            CategoryHeader(stringResource(R.string.header_behavior))
            SettingsRow(
                label = stringResource(R.string.use_mock_data),
                checked = state.isMockEnabled,
                onCheckedChange = { onSettingChange(AppSetting.MOCK_DATA, it) },
                onInfoClick = { onInfoClick(R.string.use_mock_data, R.string.info_use_mock_body) }
            )
            SettingsRow(
                label = stringResource(R.string.overwrite),
                checked = state.isOverwrite,
                onCheckedChange = { onSettingChange(AppSetting.OVERWRITE, it) },
                onInfoClick = { onInfoClick(R.string.overwrite, R.string.info_overwrite_body) }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_is_persistent),
                checked = state.isPersistent,
                onCheckedChange = { onSettingChange(AppSetting.PERSISTENT, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_is_persistent,
                        R.string.info_persistent_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.multiline_text),
                checked = state.isMultiline,
                onCheckedChange = { onSettingChange(AppSetting.MULTILINE, it) },
                onInfoClick = { onInfoClick(R.string.multiline_text, R.string.info_multiline_body) }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_full_screen_intent),
                checked = state.isFullScreen,
                onCheckedChange = { onSettingChange(AppSetting.FULL_SCREEN, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_full_screen_intent,
                        R.string.info_full_screen_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_group_notifications),
                checked = state.isGrouped,
                onCheckedChange = { onSettingChange(AppSetting.GROUPED, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_group_notifications,
                        R.string.info_grouped_body
                    )
                }
            )

            // CATEGORY: TIMING
            CategoryHeader(stringResource(R.string.header_timing))
            PtSettingsDropdown(
                label = stringResource(R.string.sending_periods),
                options = stringArrayResource(R.array.periods_array),
                selectedPosition = state.periodsPos,
                onSelectionChange = { onSettingChange(AppSetting.PERIODS, it) },
                onInfoClick = { onInfoClick(R.string.sending_periods, R.string.info_periods_body) }
            )
            PtSettingsDropdown(
                label = stringResource(R.string.sending_delays),
                options = stringArrayResource(R.array.delays_array),
                selectedPosition = state.delaysPos,
                onSelectionChange = { onSettingChange(AppSetting.DELAYS, it) },
                onInfoClick = { onInfoClick(R.string.sending_delays, R.string.info_delays_body) }
            )

            // CATEGORY: VISUAL STYLE
            CategoryHeader(stringResource(R.string.header_visual_style))
            SettingsRow(
                label = stringResource(R.string.lbl_use_big_text),
                checked = state.useBigText,
                onCheckedChange = { onSettingChange(AppSetting.BIG_TEXT, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_use_big_text,
                        R.string.info_big_text_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_show_big_picture),
                checked = state.showBigPicture,
                onCheckedChange = { onSettingChange(AppSetting.BIG_PICTURE, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_show_big_picture,
                        R.string.info_big_picture_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_use_inbox_style),
                checked = state.useInboxStyle,
                onCheckedChange = { onSettingChange(AppSetting.INBOX_STYLE, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_use_inbox_style,
                        R.string.info_inbox_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_include_actions),
                checked = state.includeActions,
                onCheckedChange = { onSettingChange(AppSetting.ACTIONS, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_include_actions,
                        R.string.info_actions_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_show_subtext),
                checked = state.showSubtext,
                onCheckedChange = { onSettingChange(AppSetting.SUBTEXT, it) },
                onInfoClick = { onInfoClick(R.string.lbl_show_subtext, R.string.info_subtext_body) }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_show_large_icon),
                checked = state.showLargeIcon,
                onCheckedChange = { onSettingChange(AppSetting.LARGE_ICON, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_show_large_icon,
                        R.string.info_large_icon_body
                    )
                }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_use_chronometer),
                checked = state.useChronometer,
                onCheckedChange = { onSettingChange(AppSetting.CHRONOMETER, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_use_chronometer,
                        R.string.info_chronometer_body
                    )
                }
            )

            // CATEGORY: SENSORY
            CategoryHeader(stringResource(R.string.header_sensory))
            SettingsRow(
                label = stringResource(R.string.vibration),
                checked = state.vibrationOn,
                onCheckedChange = { onSettingChange(AppSetting.VIBRATION, it) },
                onInfoClick = { onInfoClick(R.string.vibration, R.string.info_vibration_body) }
            )
            SettingsRow(
                label = stringResource(R.string.lbl_enable_sound),
                checked = state.enableSound,
                onCheckedChange = { onSettingChange(AppSetting.SOUND, it) },
                onInfoClick = { onInfoClick(R.string.lbl_enable_sound, R.string.info_sound_body) }
            )

            // CATEGORY: SYSTEM CONFIG
            CategoryHeader(stringResource(R.string.header_system_config))
            PtSettingsDropdown(
                label = stringResource(R.string.notification_importance),
                options = stringArrayResource(R.array.importance_array),
                selectedPosition = state.importancePos,
                onSelectionChange = { onSettingChange(AppSetting.IMPORTANCE, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.notification_importance,
                        R.string.info_importance_body
                    )
                }
            )
            PtSettingsDropdown(
                label = stringResource(R.string.lbl_lockscreen_visibility),
                options = stringArrayResource(R.array.visibility_array),
                selectedPosition = state.visibilityPos,
                onSelectionChange = { onSettingChange(AppSetting.VISIBILITY, it) },
                onInfoClick = {
                    onInfoClick(
                        R.string.lbl_lockscreen_visibility,
                        R.string.info_visibility_body
                    )
                }
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_vertical_large)))
        }
    }
}

/**
 * Technical Helper: Isolated logic for the FSI permission check.
 */
private fun checkAndRequestFullScreenIntent(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Only launch if NOT already allowed
        if (!manager.canUseFullScreenIntent()) {
            Toast.makeText(context, context.getString(R.string.warn_grant_full_screen), Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT).apply {
                data = "package:${context.packageName}".toUri()
            }
            context.startActivity(intent)
        }
    }
}

/**
 * Opens the specific Xiaomi/OEM 'Other Permissions' page if possible,
 * otherwise opens the general App Info page.
 */
private fun openOtherPermissionsSettings(context: Context) {
    try {
        // Specifically for Xiaomi/HyperOS/MIUI
        val intent = Intent("miui.intent.action.APP_OPS_SETTINGS").apply {
            putExtra("extra_pkgname", context.packageName)
        }
        context.startActivity(intent)

    } catch (e: Exception) {
        Log.e("PT_DEBUG", "ERROR: ${e.message}")

        // Fallback for some manufacturers with custom settings: Open the general App Info page
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
        }

        context.startActivity(intent)
    }
}

private fun requestIgnoreBatteryOptimizations(context: Context) {
    val pm = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
    // THE FIX: Check if we are ALREADY ignoring optimizations
    if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)

        Toast.makeText(context, context.getString(R.string.msg_battery_unrestricted_instruction), Toast.LENGTH_LONG).show()
    }
}


// PREVIEW

@Preview(name = "Full Settings - Light", showBackground = true)
@Composable
private fun SettingsBottomSheetContentPreview() {
    PushesTestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            SettingsBottomSheetContent(
                state = SettingsState(isMockEnabled = true),
                onSettingChange = { _, _ -> },
                onInfoClick = { _, _ -> },
                onClose = {},
                onReset = {}
            )
        }
    }
}
