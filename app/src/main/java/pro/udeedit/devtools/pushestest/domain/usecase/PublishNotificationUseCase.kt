package pro.udeedit.devtools.pushestest.domain.usecase

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.createBitmap
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.ui.ComposeMainActivity
import pro.udeedit.devtools.pushestest.ui.SettingsState
import pro.udeedit.devtools.pushestest.utils.*

/**
 * Use Case responsible for constructing and dispatching system notifications.
 *
 * This class encapsulates complex Android-specific logic to ensure a consistent
 * experience across different OS versions and manufacturer-specific UI skins:
 * - Implements dynamic truncation logic to preserve expansion UI.
 * - Manages real-time XML-to-Bitmap conversion for rich content.
 * - Orchestrates High-Priority Full-Screen Intents (FSI) for lock-screen alerts.
 * - Handles notification bundling and group summaries.
 */
class PublishNotificationUseCase(private val context: Context) {

    /**
     * Initializes and registers the required notification channels with the OS.
     *
     * Since Android 8.0 (API 26), importance levels are locked to the channel
     * upon creation. This method pre-registers channels for all available
     * importance levels (Urgent to Low) to allow for immediate testing.
     */
    fun initializeChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importanceNames = context.resources.getStringArray(R.array.importance_array)
            val importanceValues = context.resources.getIntArray(R.array.importance_values_array)

            for (pos in importanceNames.indices) {
                val channelId = "${CHANNEL_ID}_$pos"
                val channelName = importanceNames[pos]

                val channel = NotificationChannel(channelId, channelName, importanceValues[pos]).apply {
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    enableVibration(true)
                    setShowBadge(true)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    /**
     * Builds and publishes a notification based on the provided [SettingsState].
     *
     * Execution Steps:
     * 1. Resolve target Channel ID (Forces 'Urgent' if Full-Screen is enabled).
     * 2. Configure PendingIntents for standard and full-screen triggers.
     * 3. Construct Group Summary if bundling is active.
     * 4. Apply visual styles (BigText, BigPicture, or Inbox).
     * 5. Set system-level flags (Chronometer, LargeIcon, Action Buttons).
     * 6. Dispatch via NotificationManager.
     */
    @SuppressLint("FullScreenIntentPolicy")
    operator fun invoke(state: SettingsState) {
        val notificationManager = NotificationManagerCompat.from(context)

        // CHANNEL RESOLUTION
        var importancePos = state.importancePos
        if (state.isFullScreen) importancePos = 0
        val activeChannelId = "${CHANNEL_ID}_$importancePos"
        val visibilityValues = context.resources.getIntArray(R.array.visibility_values_array)

        // INTENT CONFIGURATION
        val intent = Intent(context, ComposeMainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val fullScreenIntent = Intent(context, ComposeMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // BUNDLING (GROUPING) LOGIC
        if (state.isGrouped) {
            val summaryNotification = NotificationCompat.Builder(context, activeChannelId)
                .setSmallIcon(R.drawable.outline_notifications_active_24)
                .setColor(context.getColor(R.color.md_theme_primary))
                .setSubText(PtLocaleUtils.getEnglishString(context, R.string.app_name))
                .setStyle(NotificationCompat.InboxStyle()
                    .setSummaryText(PtLocaleUtils.getEnglishString(context, R.string.app_name)))
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(SUMMARY_ID, summaryNotification)
            }
        }

        // METADATA & HINT CALCULATION
        val hintString = if (state.useBigText || state.showBigPicture || state.useInboxStyle || state.includeActions) {
            context.getString(R.string.hint_expandable)
        } else null

        val appName = if (state.showSubtext) PtLocaleUtils.getEnglishString(context, R.string.app_name) else null

        val finalSubText = when {
            appName != null && hintString != null -> "$appName • $hintString"
            appName != null -> appName
            else -> hintString
        }

        // CORE BUILDER CONSTRUCTION
        val builder = NotificationCompat.Builder(context, activeChannelId)
            .setSmallIcon(R.drawable.outline_notifications_active_24)
            .setColor(context.getColor(R.color.md_theme_primary))
            .setContentTitle(state.notificationTitle)
            .setContentIntent(pendingIntent)
            .setAutoCancel(!state.isPersistent)
            .setOngoing(state.isPersistent)
            .setSilent(!state.enableSound)
            .setUsesChronometer(state.useChronometer)

        if (state.visibilityPos in visibilityValues.indices) {
            builder.setVisibility(visibilityValues[state.visibilityPos])
        }

        // Apply 40/60 character truncation to ensure the OS expands the notification
        val limit = context.resources.getInteger(R.integer.body_truncation_limit)
        val teaser = if (state.useBigText && state.notificationBody.length > limit) {
            state.notificationBody.take(limit) + "..."
        } else state.notificationBody

        builder.setContentText(if (hintString != null) "$teaser ($hintString)" else teaser)
        builder.setSubText(finalSubText)

        // Apply mutually exclusive visual styles
        applyStyles(builder, state, finalSubText)

        // SYSTEM OVERRIDES & EXTRAS
        if (state.isGrouped) builder.setGroup(GROUP_KEY)

        if (state.showLargeIcon) {
            val primaryColor = context.getColor(R.color.md_theme_primary)
            builder.setLargeIcon(getBitmapFromDrawable(context, R.drawable.rounded_close_24, primaryColor))
        }

        if (state.includeActions) {
            builder.addAction(R.drawable.rounded_close_24, context.getString(R.string.lbl_stop_sending), pendingIntent)
        }

        if (state.isFullScreen && (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).canUseFullScreenIntent())) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
            builder.priority = NotificationCompat.PRIORITY_MAX
            builder.setCategory(NotificationCompat.CATEGORY_ALARM)
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            builder.setDefaults(Notification.DEFAULT_ALL)
        }

        // FINAL DISPATCH
        val id = if (state.isOverwrite && !state.isGrouped) DEFAULT_NOTIF_ID else System.currentTimeMillis().toInt()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(id, builder.build())
        }
    }

    /**
     * Applies complex visual styles to the notification builder.
     *
     * Handles:
     * - BigPictureStyle with adaptive aspect-ratio padding.
     * - BigTextStyle for large content blocks.
     * - InboxStyle with manual line-chunking to bypass standard layout limits.
     */
    private fun applyStyles(builder: NotificationCompat.Builder, state: SettingsState, subtext: String?) {
        when {
            state.showBigPicture -> {
                val originalBitmap = getBitmapFromDrawable(context, R.drawable.outline_notifications_active_24)
                if (originalBitmap != null) {
                    val isTablet = context.resources.configuration.smallestScreenWidthDp >= 600
                    val ratio = if (isTablet) 3.0f else 2.2f

                    val width = (originalBitmap.width * ratio).toInt()
                    val height = originalBitmap.height

                    val paddedBitmap = createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(paddedBitmap)
                    canvas.drawBitmap(originalBitmap, (width - originalBitmap.width) / 2f, 0f, null)

                    builder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(paddedBitmap)
                        .setBigContentTitle(state.notificationTitle)
                        .setSummaryText(state.notificationBody))
                }
            }

            state.useBigText -> {
                builder.setStyle(NotificationCompat.BigTextStyle()
                    .bigText(state.notificationBody)
                    .setSummaryText(subtext))
            }

            state.useInboxStyle -> {
                val inbox = NotificationCompat.InboxStyle().setSummaryText(subtext)
                val limit = context.resources.getInteger(R.integer.body_truncation_limit)

                state.notificationBody.split("\n").take(3).forEach { line ->
                    if (line.length > limit) {
                        line.chunked(limit).take(2).forEach { inbox.addLine(it) }
                    } else inbox.addLine(line)
                }

                inbox.addLine("Testing line 2...")

                builder.setStyle(inbox)
            }
        }
    }
}
