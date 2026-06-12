package pro.udeedit.devtools.pushestest.ui.screens

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.MainViewModel
import pro.udeedit.devtools.pushestest.ui.SettingsState
import pro.udeedit.devtools.pushestest.ui.components.CategoryHeader
import pro.udeedit.devtools.pushestest.ui.components.PtSettingsDropdown
import pro.udeedit.devtools.pushestest.ui.components.SettingsHeader
import pro.udeedit.devtools.pushestest.ui.components.SettingsRow
import pro.udeedit.devtools.pushestest.ui.theme.PushesTestTheme
import pro.udeedit.devtools.pushestest.utils.AppSetting

// Encapsulated ViewModel pattern
@Composable
fun SettingsBottomSheet(
    viewModel: MainViewModel,
    onInfoClick: (Int, Int) -> Unit,
    onClose: () -> Unit
) {
    // Collect the single state object
    val state by viewModel.state

    SettingsBottomSheetContent(
        state = state,
        onSettingChange = { setting, value ->
            if (value is Boolean) viewModel.set(setting, value)
            else if (value is Int) viewModel.set(setting, value)
        },
        onInfoClick = onInfoClick,
        onClose = onClose,
        onReset = { viewModel.resetToDefaults() }
    )
}


@Composable
fun SettingsBottomSheetContent(
    state: SettingsState, // Use the state object
    onSettingChange: (AppSetting, Any) -> Unit, // Unified event handler
    onInfoClick: (Int, Int) -> Unit,
    onClose: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_horizontal_middle))
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(onReset = onReset, onClose = onClose)

        // --- BEHAVIOR ---
        CategoryHeader(stringResource(R.string.header_behavior))
        SettingsRow(
            label = stringResource(R.string.use_mock_data),
            checked = state.isMockEnabled, // Read from state
            onCheckedChange = { onSettingChange(AppSetting.MOCK_DATA, it) },
            onInfoClick = { onInfoClick(R.string.use_mock_data, R.string.info_use_mock_body) }
        )
        SettingsRow(
            label = stringResource(R.string.overwrite),
            checked = state.isOverwrite,
            onCheckedChange = { onSettingChange(AppSetting.OVERWRITE, it) },
            onInfoClick = { onInfoClick(R.string.overwrite, R.string.info_overwrite_body) }
        )

        // --- TIMING ---
        CategoryHeader(stringResource(R.string.header_timing))
        PtSettingsDropdown(
            label = stringResource(R.string.sending_periods),
            options = stringArrayResource(R.array.periods_array),
            selectedPosition = state.periodsPos, // Read from state
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

        // --- 4. VISUAL STYLE ---
        CategoryHeader(stringResource(R.string.header_visual_style))
        SettingsRow(
            label = stringResource(R.string.lbl_use_big_text),
            checked = state.useBigText,
            onCheckedChange = { onSettingChange(AppSetting.BIG_TEXT, it) },
            onInfoClick = { onInfoClick(R.string.lbl_use_big_text, R.string.info_big_text_body) }
        )
        // ... (Repeat for BigPicture, Inbox, Actions, Subtext, LargeIcon, Chronometer) ...

        // --- 5. SENSORY ---
        CategoryHeader(stringResource(R.string.header_sensory))
        SettingsRow(
            label = stringResource(R.string.vibration),
            checked = state.vibrationOn,
            onCheckedChange = { onSettingChange(AppSetting.VIBRATION, it) },
            onInfoClick = { onInfoClick(R.string.vibration, R.string.info_vibration_body) }
        )

        // --- 6. SYSTEM CONFIG ---
        CategoryHeader(stringResource(R.string.header_system_config))
        PtSettingsDropdown(
            label = stringResource(R.string.notification_importance),
            options = stringArrayResource(R.array.importance_array),
            selectedPosition = state.importancePos,
            onSelectionChange = { onSettingChange(AppSetting.IMPORTANCE, it) },
            onInfoClick = { onInfoClick(R.string.notification_importance, R.string.info_importance_body) }
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.margin_vertical_large)))
    }
}


// PREVIEW

@Preview(name = "Full Settings - Light", showBackground = true)
@Composable
private fun SettingsBottomSheetContentPreview() {
    PushesTestTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            SettingsBottomSheetContent(
                state = SettingsState(isMockEnabled = true), // Safe data class
                onSettingChange = { _, _ -> },
                onInfoClick = { _, _ -> },
                onClose = {},
                onReset = {}
            )
        }
    }
}
