package pro.udeedit.devtools.pushestest.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import pro.udeedit.devtools.anarchist.AnarchistPermissionStatus
import pro.udeedit.devtools.anarchist.AnarchistPermissionUtils
import pro.udeedit.devtools.cushystorage.CushyStorage
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.databinding.ActivityMainBinding
import pro.udeedit.devtools.pushestest.utils.CHANNEL_ID
import pro.udeedit.devtools.pushestest.utils.DEFAULT_NOTIF_ID
import pro.udeedit.devtools.pushestest.utils.DEF_CHRONOMETER
import pro.udeedit.devtools.pushestest.utils.DEF_DELAYS_POS
import pro.udeedit.devtools.pushestest.utils.DEF_ENABLE_SOUND
import pro.udeedit.devtools.pushestest.utils.DEF_FULL_SCREEN
import pro.udeedit.devtools.pushestest.utils.DEF_GROUPED_NOTIFICATIONS
import pro.udeedit.devtools.pushestest.utils.DEF_IMPORTANCE_POS
import pro.udeedit.devtools.pushestest.utils.DEF_INCLUDE_ACTIONS
import pro.udeedit.devtools.pushestest.utils.DEF_IS_PERSISTENT
import pro.udeedit.devtools.pushestest.utils.DEF_OVERWRITE_NOTIFICATION
import pro.udeedit.devtools.pushestest.utils.DEF_PERIODS_POS
import pro.udeedit.devtools.pushestest.utils.DEF_SHOW_BIG_PICTURE
import pro.udeedit.devtools.pushestest.utils.DEF_SHOW_LARGE_ICON
import pro.udeedit.devtools.pushestest.utils.DEF_SHOW_SUBTEXT
import pro.udeedit.devtools.pushestest.utils.DEF_USE_BIG_TEXT
import pro.udeedit.devtools.pushestest.utils.DEF_USE_INBOX_STYLE
import pro.udeedit.devtools.pushestest.utils.DEF_USE_MOCK_DATA
import pro.udeedit.devtools.pushestest.utils.DEF_VIBRATION_ON
import pro.udeedit.devtools.pushestest.utils.DEF_VISIBILITY_POS
import pro.udeedit.devtools.pushestest.utils.PREF_CHRONOMETER
import pro.udeedit.devtools.pushestest.utils.PREF_DELAYS_POS
import pro.udeedit.devtools.pushestest.utils.PREF_ENABLE_SOUND
import pro.udeedit.devtools.pushestest.utils.PREF_FULL_SCREEN
import pro.udeedit.devtools.pushestest.utils.PREF_GROUPED_NOTIFICATIONS
import pro.udeedit.devtools.pushestest.utils.PREF_IMPORTANCE_POS
import pro.udeedit.devtools.pushestest.utils.PREF_INCLUDE_ACTIONS
import pro.udeedit.devtools.pushestest.utils.PREF_IS_PERSISTENT
import pro.udeedit.devtools.pushestest.utils.PREF_USE_MOCK_DATA
import pro.udeedit.devtools.pushestest.utils.PREF_MULTILINE_NOTIFICATION
import pro.udeedit.devtools.pushestest.utils.PREF_OVERWRITE_NOTIFICATION
import pro.udeedit.devtools.pushestest.utils.PREF_PERIODS_POS
import pro.udeedit.devtools.pushestest.utils.PREF_SHOW_BIG_PICTURE
import pro.udeedit.devtools.pushestest.utils.PREF_SHOW_LARGE_ICON
import pro.udeedit.devtools.pushestest.utils.PREF_SHOW_SUBTEXT
import pro.udeedit.devtools.pushestest.utils.PREF_USE_BIG_TEXT
import pro.udeedit.devtools.pushestest.utils.PREF_USE_INBOX_STYLE
import pro.udeedit.devtools.pushestest.utils.PREF_VIBRATION_ON
import pro.udeedit.devtools.pushestest.utils.PREF_VISIBILITY_POS
import pro.udeedit.devtools.pushestest.utils.PtLocaleUtils
import pro.udeedit.devtools.pushestest.utils.PtMockDataUtils
import pro.udeedit.devtools.pushestest.utils.REQUEST_PERMISSION_CODE
import pro.udeedit.devtools.pushestest.utils.getBitmapFromDrawable
import kotlin.jvm.java
import androidx.core.graphics.createBitmap

private const val TAG = "MainActivity"
private const val GROUP_KEY = "pro.udeedit.devtools.pushestest.WORK_GROUP"
private const val SUMMARY_ID = 9999 // Fixed ID for the group header

class MainActivity : AppCompatActivity() {

    // permissions
    val requestPermissionList: MutableList<String> = mutableListOf()

    // vibrator
    private lateinit var vibrator: Vibrator

    // pattern: wait, vibrate, wait, vibrate
    val patternConfirmation = longArrayOf(0, 100, 50, 100)

    // Pattern: vibrate-beep pattern (longer, more aggressive)
    // Vibrate 3 times with pauses
    val patternError = longArrayOf(0, 300, 100, 300, 100, 300)

    private val vibrationDurationSuccess = 500L
    private val vibrationDurationError = 1000L

    var defaultTexts: Boolean = false
    var overwriteNotification: Boolean = false
    var vibrationOnError: Boolean = false
    var multilineBody: Boolean = false

    // periodic notifications
    private var isPeriodicActive = false
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var periodicRunnable: Runnable

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setSupportActionBar(binding.toolbar)

        // setup window view
        setupWindow()
        createVibratorObject()
        createAllNotificationChannels()

        binding.btnSendNotification.setOnClickListener {
            onSendNotification()
            // test
//            publishLegacyContentInfoTest()
//            publishFixedBigTextTest()
//            publishFixedSubtextTest()
        }

        refreshSettingsFromStorage()

        binding.tilNotificationTitle.setEndIconOnClickListener {
            if (CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)) {
                val mock = PtMockDataUtils.getRandomMockData()
                binding.edtNotificationTitle.setText(mock.title)
                binding.edtNotificationBody.setText(mock.body)
            }
        }

        binding.btnStopPeriodicalNotifications.setOnClickListener {
            stopPeriodicNotifications()
            Toast.makeText(this, getString(R.string.snack_periodic_stopped), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        initializePermissionList()
        requestSinglePermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure no loops are running if the app is destroyed
        stopPeriodicNotifications()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            // Launch the Bottom Sheet instead of an Activity
            val settingsSheet = SettingsBottomSheet()
            settingsSheet.show(supportFragmentManager, SettingsBottomSheet.TAG)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Public function so the BottomSheet can tell the Activity
     * to refresh its configuration when closed.
     */
    fun refreshSettingsFromStorage() {
        retrievePreferences()
        checkPeriodicalNotificationsAlive()
        handleMockDataToggle()
        setupMultilineNotification(multilineBody)

        // Any other UI updates that depend on settings
    }

    private fun checkPeriodicalNotificationsAlive() {
        // If the loop stopped itself in the background,
        // or the user changed the setting, reset the buttons.
        val periodPos = CushyStorage.getInt(PREF_PERIODS_POS, DEF_PERIODS_POS)
        if (periodPos == 0 && isPeriodicActive) {
            stopPeriodicNotifications()
        }
    }


    private fun retrievePreferences() {
        defaultTexts = CushyStorage.getBoolean(PREF_USE_MOCK_DATA, false)
        overwriteNotification = CushyStorage.getBoolean(PREF_OVERWRITE_NOTIFICATION, false)
        vibrationOnError = CushyStorage.getBoolean(PREF_VIBRATION_ON, true)
        multilineBody = CushyStorage.getBoolean(PREF_MULTILINE_NOTIFICATION, false) ||
                CushyStorage.getBoolean(PREF_USE_BIG_TEXT, false)
    }

    private fun setupWindow() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun handleMockDataToggle() {
        val isMockEnabled = CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)
        val isBigTextMode = CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)

        // Toggle the icon visibility
        binding.tilNotificationTitle.isEndIconVisible = isMockEnabled

        binding.apply {
            // Clear red error states immediately, so they don't pop up
            tilNotificationTitle.isErrorEnabled = false
            tilNotificationBody.isErrorEnabled = false

            if (isMockEnabled) {
                // We keep it "Enabled" for readability, but "Lock" the input
                edtNotificationTitle.isFocusable = false
                edtNotificationBody.isFocusable = false

                // Subtle hint that it's "locked"
                tilNotificationTitle.alpha = 0.8f
                tilNotificationBody.alpha = 0.8f

                val mock = if (isBigTextMode) PtMockDataUtils.getRandomBigMockData() else PtMockDataUtils.getRandomMockData()
                edtNotificationTitle.setText(mock.title)
                binding.edtNotificationBody.setText(mock.body)

            } else {
                // Clear text
                edtNotificationTitle.text?.clear()
                edtNotificationBody.text?.clear()

                // Unlock input
                edtNotificationTitle.isFocusableInTouchMode = true
                edtNotificationBody.isFocusableInTouchMode = true

                tilNotificationTitle.alpha = 1.0f
                tilNotificationBody.alpha = 1.0f

                edtNotificationTitle.text?.clear()
                edtNotificationBody.text?.clear()
            }
        }
    }


    private fun setupMultilineNotification(isChecked: Boolean) {
        binding.edtNotificationBody.apply {
            if (isChecked) {
                // Expanded mode: stable at 6 lines
                setLines(6)     // Call the method directly
                minLines = 6
                maxLines = 6
            } else {
                // Standard mode: stable at 2 lines
                setLines(2)
                minLines = 2
                maxLines = 2
            }
        }
    }



    /** init vibrator object */
    @Suppress("DEPRECATION")
    fun createVibratorObject() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator = vibratorManager.defaultVibrator

        } else {
            // For earlier versions
            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

//    /** Set text watcher object for notification title */
//    fun createTextWatcherTitleObject() {
//        textWatcherTitle = object : TextWatcher {
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // ONLY clear the error if the user starts typing
//                if (!s.isNullOrBlank()) {
//                    binding.tilNotificationTitle.isErrorEnabled = false
////                    setErrorNotificationTitle(false)
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun afterTextChanged(s: Editable?) {}
//        }
//    }
//
//    /** Set text watcher object for notification body */
//    fun createTextWatcherBodyObject() {
//        textWatcherBody = object : TextWatcher {
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                // ONLY clear the error if the user starts typing
//                if (!s.isNullOrBlank()) {
//                    binding.tilNotificationBody.isErrorEnabled = false
////                    setErrorNotificationBody(false)
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun afterTextChanged(s: Editable?) {}
//        }
//    }

    /** setup notification channels for sending notification within the app
     * to channel which is actually requested */
    private fun createAllNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val importanceValues = resources.getIntArray(R.array.importance_values_array)
            val importanceNames = PtLocaleUtils.getEnglishStringArray(this, R.array.importance_array)

            for (pos in importanceNames.indices) {
                val channelId = "${CHANNEL_ID}_$pos"
                // Get value from array instead of hardcoded mapping
                val importance = importanceValues[pos]

                val appNameEnglish = PtLocaleUtils.getEnglishString(this, R.string.app_name)
                val channelName = "$appNameEnglish: ${importanceNames[pos]}"

                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                manager.createNotificationChannel(channel)
            }
        }
    }


    /** check if notification fields are correct and send notification */
    private fun onSendNotification() {
        val periodPos = CushyStorage.getInt(PREF_PERIODS_POS, DEF_PERIODS_POS)
        val periodValues = resources.getIntArray(R.array.periods_values_array)
        val periodMs = if (periodPos in periodValues.indices) periodValues[periodPos].toLong() else 0L

        if (periodMs > 0) {
            startPeriodicNotifications(periodMs)

        } else {
            // --- Single-shot logic ---

            // Check for Mock Data setting
            val isMockEnabled = CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)
            val isBigTextMode = CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)

            val title: String
            val body: String

            if (isMockEnabled) {
                // Choose mock data based on the Visual Style setting
                val mock = if (isBigTextMode) {
                    PtMockDataUtils.getRandomBigMockData()
                } else {
                    PtMockDataUtils.getRandomMockData()
                }

                title = mock.title
                body = mock.body
                binding.edtNotificationTitle.setText(title)
                binding.edtNotificationBody.setText(body)

            } else {
                title = binding.edtNotificationTitle.text.toString()
                body = binding.edtNotificationBody.text.toString()
            }

            // Validation warning (non-blocking)
            if (title.isBlank() || body.isBlank()) {
                Toast.makeText(this, getString(R.string.warn_empty_fields), Toast.LENGTH_SHORT).show()
                vibrateError()

            // Optional: show the red error layouts briefly without blocking
//            if (title.isBlank()) setErrorNotificationTitle(true)
//            if (body.isBlank()) setErrorNotificationBody(true)
            }

            val delayPos = CushyStorage.getInt(PREF_DELAYS_POS, DEF_DELAYS_POS)
            val delayValues = resources.getIntArray(R.array.delays_values_array)
            val delayMs = if (delayPos in delayValues.indices) delayValues[delayPos].toLong() else 0L

            if (delayMs > 0) {
                val seconds = (delayMs / 1000).toInt()
                Toast.makeText(this, getString(R.string.snack_scheduled, seconds), Toast.LENGTH_SHORT).show()
                handler.postDelayed({ publishNotification(title, body) }, delayMs)

            } else {
                publishNotification(title, body)
            }
        }
    }

    private fun startPeriodicNotifications(interval: Long) {
        isPeriodicActive = true
        swapButtons(true) // Animate to Stop mode

        // Toggle UI: Show Stop, Hide Send
        binding.btnStopPeriodicalNotifications.visibility = View.VISIBLE
        binding.btnSendNotification.visibility = View.GONE
//        binding.btnSendNotification.isEnabled = false // Prevent double starting

        periodicRunnable = object : Runnable {
            override fun run() {
                // 1. THE CRITICAL CHECK:
                // Check the source of truth (storage) every single time the loop fires.
                val currentPeriodPos = CushyStorage.getInt(PREF_PERIODS_POS, DEF_PERIODS_POS)

                // 2. If it was set to 0 (Single-shot) or the local flag is false, KILL the loop.
                if (currentPeriodPos == 0 || !isPeriodicActive) {
                    stopPeriodicNotifications()
                    return
                }

                // 3. Continue with the notification
                if (CushyStorage.getBoolean(PREF_USE_MOCK_DATA, DEF_USE_MOCK_DATA)) {
                    val isBig = CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)
                    val mock = if (isBig) PtMockDataUtils.getRandomBigMockData() else PtMockDataUtils.getRandomMockData()
                    binding.edtNotificationTitle.setText(mock.title)
                    binding.edtNotificationBody.setText(mock.body)
                }

                val title = binding.edtNotificationTitle.text.toString()
                val body = binding.edtNotificationBody.text.toString()
                publishNotification(title, body)

                // 4. Schedule the next one only if we are still active
                handler.postDelayed(this, interval)
            }
        }

        // Fire the first one immediately
        handler.post(periodicRunnable)
    }

    private fun stopPeriodicNotifications() {
        isPeriodicActive = false

        // Check if the runnable has actually been created before removing it
        if (::periodicRunnable.isInitialized) {
            handler.removeCallbacks(periodicRunnable)
        }

        // Toggle UI: Show Send, Hide Stop
        binding.btnStopPeriodicalNotifications.visibility = View.GONE
        binding.btnSendNotification.visibility = View.VISIBLE
//        binding.btnSendNotification.isEnabled = true
        swapButtons(false) // Animate back to Send mode
    }


    /** publish actual notification */
    private fun publishNotification(title: String, body: String) {
        val notificationManager = NotificationManagerCompat.from(this)

        // 1. Get User Preferences
        var importancePos = CushyStorage.getInt(PREF_IMPORTANCE_POS, DEF_IMPORTANCE_POS)
        val visibilityPos = CushyStorage.getInt(PREF_VISIBILITY_POS, DEF_VISIBILITY_POS)

        val isPersistent = CushyStorage.getBoolean(PREF_IS_PERSISTENT, DEF_IS_PERSISTENT)
        val isGrouped = CushyStorage.getBoolean(PREF_GROUPED_NOTIFICATIONS, DEF_GROUPED_NOTIFICATIONS)
        val isFullScreen = CushyStorage.getBoolean(PREF_FULL_SCREEN, DEF_FULL_SCREEN)
        val isOverwrite = CushyStorage.getBoolean(PREF_OVERWRITE_NOTIFICATION, DEF_OVERWRITE_NOTIFICATION)

        val isBigText = CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)
        val isBigPicture = CushyStorage.getBoolean(PREF_SHOW_BIG_PICTURE, DEF_SHOW_BIG_PICTURE)
        val isInbox = CushyStorage.getBoolean(PREF_USE_INBOX_STYLE, DEF_USE_INBOX_STYLE)
        val hasActions = CushyStorage.getBoolean(PREF_INCLUDE_ACTIONS, DEF_INCLUDE_ACTIONS)

        // 2. Load technical values from your integer-arrays
        val visibilityValues = resources.getIntArray(R.array.visibility_values_array)

        // Logic Refinement: Full-screen intents REQUIRE High/Urgent importance to work.
        if (isFullScreen) {
            importancePos = 0 // Force to 'Urgent' channel (Index 0)
        }

        val activeChannelId = "${CHANNEL_ID}_$importancePos"

        // Build the Intent (Tap Action)
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. Handle Grouping Summary
        if (isGrouped) {
            val summaryNotification = NotificationCompat.Builder(this, activeChannelId)
                .setSmallIcon(R.drawable.outline_notifications_active_24)
                // 1. ContentTitle and Text are usually ignored, but some OEMs show them
                .setContentTitle(title)
//                .setContentText("Bundled messages")
                // 2. This is the one that DEFINITELY shows up next to the app name
                .setSubText(PtLocaleUtils.getEnglishString(this, R.string.app_name))
                // 3. This style is the standard for bundles
                .setStyle(NotificationCompat.InboxStyle()
                    .setSummaryText(PtLocaleUtils.getEnglishString(this, R.string.app_name)))
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(SUMMARY_ID, summaryNotification)
            }
        }

//        if (isGrouped) {
//            // 1. Create the SUMMARY (Header) using InboxStyle
//            // This is what pros do to make the group look great
//            val summaryStyle = NotificationCompat.InboxStyle()
//                .setSummaryText(PtLocaleUtils.getEnglishString(this, R.string.app_name))
//                // This is a "Pro" touch: the summary shows the most recent title
//                .setBigContentTitle(title)
////
////            val summaryStyle = NotificationCompat.InboxStyle()
////                .setSummaryText(PtLocaleUtils.getEnglishString(this, R.string.app_name))
////                .addLine(title) // You can mock these
////                .addLine(body)
//
//            val summaryNotification = NotificationCompat.Builder(this, activeChannelId)
//                .setSmallIcon(R.drawable.outline_notifications_active_24)
//                .setStyle(summaryStyle) // <--- Applying InboxStyle here!
//                .setGroup(GROUP_KEY)
//                .setGroupSummary(true)
//                .setAutoCancel(true)
//                .build()
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//                notificationManager.notify(SUMMARY_ID, summaryNotification)
//            }
//        }

        // 4. Main Notification Builder
        val builder = NotificationCompat.Builder(this, activeChannelId)
            .setSmallIcon(R.drawable.outline_notifications_active_24)
            .setContentTitle(title)
//            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(!isPersistent)
            .setOngoing(isPersistent)
            .setSilent(!CushyStorage.getBoolean(PREF_ENABLE_SOUND, DEF_ENABLE_SOUND))

        // Set Visibility from your array mapping
        if (visibilityPos in visibilityValues.indices) {
            builder.setVisibility(visibilityValues[visibilityPos])
        }

        // --- UNIFIED VISUAL STYLES & HINT LOGIC ---

        // PREF_USE_BIG_TEXT, PREF_SHOW_BIG_PICTURE and PREF_USE_INBOX_STYLE
        // are mutually excluded in settings!

//        // 1. Calculate the hint/header metadata
//        val hintString = if (isBigText || isBigPicture || isInbox || hasActions) getString(R.string.hint_expandable) else null
//        val appName = if (CushyStorage.getBoolean(PREF_SHOW_SUBTEXT, DEF_SHOW_SUBTEXT)) PtLocaleUtils.getEnglishString(this, R.string.app_name) else null
//
//        val finalSubText = when {
//            appName != null && hintString != null -> "$appName • $hintString"
//            appName != null -> appName
//            else -> hintString
//        }
//
//        // This is the "D2D Trick" that just worked in our test
//        val displayBody = if (hintString != null) "$body ($hintString)" else body
//        builder.setContentText(displayBody)
//        builder.setSubText(finalSubText) // Keep the header hint too

        // --- 1. Calculate metadata (Lines 548 - 557) ---
        val hintString = if (isBigText || isBigPicture || isInbox || hasActions) getString(R.string.hint_expandable) else null
        val appName = if (CushyStorage.getBoolean(PREF_SHOW_SUBTEXT, DEF_SHOW_SUBTEXT)) PtLocaleUtils.getEnglishString(this, R.string.app_name) else null
        val truncationLimit = resources.getInteger(R.integer.body_truncation_limit)

        val finalSubText = when {
            appName != null && hintString != null -> "$appName • $hintString"
            appName != null -> appName
            else -> hintString
        }

        Log.d(TAG, "finalSubText = $finalSubText")
        // Apply teaser truncation ONLY for the BigText case
        val teaser = if (isBigText && body.length > truncationLimit) {
            body.take(truncationLimit) + "..."
        } else {
            body
        }

        // Set the collapsed content text with the hint
        val displayBody = if (hintString != null) "$teaser ($hintString)" else teaser
        builder.setContentText(displayBody)

        builder.setSubText(finalSubText)    // Set the header hint

        // 3. Apply the Style (This is the working BigText block)
        when {
            isBigText -> {
                Log.d(TAG, "isBigText")
                builder.setStyle(NotificationCompat.BigTextStyle()
                    .bigText(body)                   // The full original text
                    .setBigContentTitle(title)
                    .setSummaryText(finalSubText))   // Hint stays at top when expanded
            }

            isBigPicture -> {
                // TODO: test icon
                // Use the helper instead of decodeResource
                val originalBitmap = getBitmapFromDrawable(applicationContext, R.drawable.ic_setting_2040504)
//                val originalBitmap = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.ic_setting_2040504)

                if (originalBitmap != null) {
                    // --- THE D2D "NO-CROP" FIX ---
                    // We create a new 2:1 ratio bitmap (e.g., 1024 x 512)
                    // and place your square 512x512 icon in the center.
                    val width = originalBitmap.width * 2
                    val height = originalBitmap.height

                    val paddedBitmap = createBitmap(width, height)
                    val canvas = android.graphics.Canvas(paddedBitmap)

                    // Draw the original square icon in the middle of the wide rectangle
                    val left = (width - originalBitmap.width) / 2f
                    canvas.drawBitmap(originalBitmap, left, 0f, null)

                    builder.setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigPicture(paddedBitmap) // Use the padded wide version
                            .setBigContentTitle(title)
                            .setSummaryText(body)
                    )
                }
            }

            isInbox -> {
                val inbox = NotificationCompat.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText(finalSubText)

                // Using your constant (40) ensures it fits on most screens
                val manualLines = body.split("\n")

                for (manualLine in manualLines.take(3)) {
                    if (manualLine.length > truncationLimit) {
                        // Use the same constant here for a uniform look
                        manualLine.chunked(truncationLimit).take(3).forEach { chunk ->
                            inbox.addLine(chunk)
                        }
                    } else if (manualLine.isNotBlank()) {
                        inbox.addLine(manualLine)
                    }
                }

                inbox.addLine("Testing line 2...")
                builder.setStyle(inbox)
            }
        }

//        if (CushyStorage.getBoolean(PREF_USE_BIG_TEXT, DEF_USE_BIG_TEXT)) {
//            builder.setStyle(NotificationCompat.BigTextStyle()
//                .bigText(body)
//                .setBigContentTitle(title) // Changes title when expanded
//                .setSummaryText("Swipe up to hide details")) // Adds a tiny hint at the bottom
//        }
//
//        if (CushyStorage.getBoolean(PREF_SHOW_BIG_PICTURE, DEF_SHOW_BIG_PICTURE)) {
//            val bitmap = android.graphics.BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
//            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
//        }
//
//        if (CushyStorage.getBoolean(PREF_USE_INBOX_STYLE, DEF_USE_INBOX_STYLE)) {
//            // InboxStyle allows up to ~6 lines of text
//            val inbox = NotificationCompat.InboxStyle()
//                .setBigContentTitle("New Messages") // Header when expanded
//                .setSummaryText("user@example.com") // Extra info at top
//                .addLine(title) // Line 1
//                .addLine(body)  // Line 2
//                .addLine("Check recent logs...") // Line 3
//            builder.setStyle(inbox)
//        }

        // 6. Apply Non-Style Options

        if (isGrouped) builder.setGroup(GROUP_KEY)

        if (CushyStorage.getBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)) {
            // Use a DIFFERENT drawable than your small icon (alarm bell)
            val bitmap = getBitmapFromDrawable(applicationContext, R.drawable.rounded_close_24)
//            val bitmap = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.rounded_close_24)
            builder.setLargeIcon(bitmap)
        }

        if (CushyStorage.getBoolean(PREF_CHRONOMETER, DEF_CHRONOMETER)) {
            builder.setUsesChronometer(true)
        }

        // TODO: remove it
//        if (CushyStorage.getBoolean(PREF_SHOW_CONTENT_INFO, DEF_SHOW_CONTENT_INFO)) {
//            builder.setContentInfo("99+")
//        }

        if (CushyStorage.getBoolean(PREF_INCLUDE_ACTIONS, DEF_INCLUDE_ACTIONS)) {
            builder.addAction(R.drawable.rounded_close_24, getString(R.string.lbl_stop_sending), pendingIntent)
        }

        // Alarm-style Interruption
        if (isFullScreen) {
            // Only set this if the OS allows it (or if it's below Android 14)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                (getSystemService(NotificationManager::class.java)).canUseFullScreenIntent()
            ) {
                builder.setFullScreenIntent(pendingIntent, true)
                builder.setCategory(NotificationCompat.CATEGORY_ALARM)
                builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            }
        }

        // 7. Permission Check and Final Dispatch

        // If grouped, we MUST use unique IDs to see the bundle. Otherwise, respect overwrite.
        val notificationId = if (isOverwrite && !isGrouped) {
            DEFAULT_NOTIF_ID
        } else {
            System.currentTimeMillis().toInt()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // test
            // Toast the EXACT strings being sent to the OS
//            Toast.makeText(this, "Channel: $activeChannelId | Importance: $importancePos", Toast.LENGTH_LONG).show()

            notificationManager.notify(notificationId, builder.build())
            vibrateSuccess()

            Snackbar.make(binding.root, getString(R.string.snack_notif_sent), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.snack_action_dismiss_all)) {
                    notificationManager.cancelAll()
                }.show()
        }
    }


    @Suppress("DEPRECATION")
    private fun vibrateError() {
        // Only proceed if vibration setting is enabled
        if (!CushyStorage.getBoolean(PREF_VIBRATION_ON, DEF_VIBRATION_ON)) return

        Log.d(TAG, "vibrateError")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(patternError, -1))
        } else {
            vibrator.vibrate(vibrationDurationError)
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrateSuccess() {
        // Only proceed if vibration setting is enabled
        if (!CushyStorage.getBoolean(PREF_VIBRATION_ON, DEF_VIBRATION_ON)) return

        Log.d(TAG, "vibrateSuccess")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(patternConfirmation, -1))
        } else {
            vibrator.vibrate(vibrationDurationSuccess)
        }
    }


    // handle permissions

    private fun initializePermissionList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionList.add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /** request single permission for POST_NOTIFICATIONS */
    private fun requestSinglePermission() {
        val permissionResult = AnarchistPermissionUtils.checkAndRequestPermissions(
                this,
                requestPermissionList,
                REQUEST_PERMISSION_CODE
            )

        when (permissionResult.finalStatus) {
            AnarchistPermissionStatus.ALLOWED -> {
                Log.d(TAG, "Permission is already allowed by user")
            }

            AnarchistPermissionStatus.DENIED_PERMANENTLY -> {
                //Request user to allow permission by sending to permission list page
                //You can show customized dialog and then call this function
                Toast.makeText(this, getString(R.string.permission_is_permanently_denied), Toast.LENGTH_LONG)
                    .show()

                AnarchistPermissionUtils.askUserToRequestPermissionExplicitly(this)
            }

            else -> {
                // Permission is requesting for first time or user denied permission before but not permanently
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            // Check status after user allowed or denied permission using the same way while requested permission
            val permissionResult = AnarchistPermissionUtils.checkAndRequestPermissions(
                    this,
                    requestPermissionList,
                    REQUEST_PERMISSION_CODE,
                    checkStatusOnly = true
                )

            when (permissionResult.finalStatus) {
                AnarchistPermissionStatus.ALLOWED -> {
                    Toast.makeText(this, getString(R.string.permission_allowed), Toast.LENGTH_LONG).show()
                }

                AnarchistPermissionStatus.DENIED_PERMANENTLY -> {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_is_permanently_denied),
                        Toast.LENGTH_LONG
                    ).show()

                } else -> {
                    // Permission denied by user but not permanently
                }
            }
        }
    }


    // Helper function to animate the button swap
    private fun swapButtons(showStop: Boolean) {
        val duration = 450L // Slightly longer for the "swag" to feel fluid
        val springBounciness = 2.0f

        // Convert 20dp to pixels for a consistent vertical offset across devices
        val offset = 20f * resources.displayMetrics.density

        if (showStop) {
            // --- 1. Animate SEND button out (Sliding UP and out) ---
            binding.btnSendNotification.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .translationY(-offset) // Slide slightly UP
                .setDuration(250)
                .withEndAction {
                    binding.btnSendNotification.visibility = View.GONE
                    binding.btnSendNotification.translationY = 0f // Reset for next time
                }
                .start()

            // --- 2. Animate STOP button in (Springing UP from bottom) ---
            binding.btnStopPeriodicalNotifications.apply {
                alpha = 0f
                scaleX = 0.7f
                scaleY = 0.7f
                translationY = offset // Start below its final position
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f) // Settle at original position
                    .setDuration(duration)
                    .setInterpolator(android.view.animation.OvershootInterpolator(springBounciness))
                    .start()
            }
        } else {
            // --- 3. Animate STOP button out (Sliding UP and out) ---
            binding.btnStopPeriodicalNotifications.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .translationY(-offset)
                .setDuration(250)
                .withEndAction {
                    binding.btnStopPeriodicalNotifications.visibility = View.GONE
                    binding.btnStopPeriodicalNotifications.translationY = 0f
                }
                .start()

            // --- 4. Animate SEND button back in (Springing UP from bottom) ---
            binding.btnSendNotification.apply {
                alpha = 0f
                scaleX = 0.7f
                scaleY = 0.7f
                translationY = offset
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0f)
                    .setDuration(duration)
                    .setInterpolator(android.view.animation.OvershootInterpolator(springBounciness))
                    .start()
            }
        }
    }



//    private fun publishLegacyContentInfoTest() {
//        val activeChannelId = "${CHANNEL_ID}_0"
//
//        val builder = NotificationCompat.Builder(this, activeChannelId)
//            .setSmallIcon(R.drawable.outline_notifications_active_24)
//            .setContentTitle("Legacy Test")
//            .setContentText("Testing ContentInfo visibility")
//            // 1. DO NOT call setSubText at all
//            // 2. Disable the timestamp (sometimes they fight for space)
//            .setShowWhen(false)
//            // 3. The Test Field
//            .setContentInfo("99+")
//            // 4. Try the "Number" field too (often used for counts)
//            .setNumber(99)
//            .setAutoCancel(true)
//
//        val notificationManager = NotificationManagerCompat.from(this)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            notificationManager.notify(5555, builder.build())
//        }
//    }

//    private fun publishFixedSubtextTest() {
//        val activeChannelId = "${CHANNEL_ID}_0"
//        val title = "Subtext Style Hack"
//        val body = "Testing if style forces subtext visibility."
//        val mySubtext = "Pushes Test Header"
//
//        val builder = NotificationCompat.Builder(this, activeChannelId)
//            .setSmallIcon(R.drawable.outline_notifications_active_24)
//            .setContentTitle(title)
//            .setContentText(body)
//            // 1. Set standard subtext
//            .setSubText(mySubtext)
//            // 2. Set the SAME text as a Style Summary (This is the trick)
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText(body)
//                .setSummaryText(mySubtext))
//            .setAutoCancel(true)
//
//        // --- THE "XIAOMI ICON FIX" ---
//        // If the user hasn't selected a specific Large Icon,
//        // we fallback to the Launcher Icon so it looks "good" on the left.
////        val largeIconBitmap = if (CushyStorage.getBoolean(PREF_SHOW_LARGE_ICON, DEF_SHOW_LARGE_ICON)) {
////            // Use the "X" icon (or whatever the user selected for testing)
////            android.graphics.BitmapFactory.decodeResource(resources, R.drawable.rounded_close_24)
////        } else {
////            // DEFAULT: Use the actual App Launcher Icon so it looks like a real app
////            android.graphics.BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
////        }
//
//        val largeIconBitmap = android.graphics.BitmapFactory.decodeResource(resources, R.drawable.rounded_close_24)
//        builder.setLargeIcon(largeIconBitmap)
//
//        val notificationManager = NotificationManagerCompat.from(this)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            notificationManager.notify(7777, builder.build())
//        }
//    }

//    private fun publishFixedBigTextTest() {
//        val activeChannelId = "${CHANNEL_ID}_0" // Force Urgent Channel
//        val title = "D2D BigText Test"
//        val body = "Line 1: System started\nLine 2: Logs processing\nLine 3: Cache cleared\nLine 4: Database optimized\nLine 5: Security check complete\nLine 6: Port 8080 open\nLine 7: Ready for testing."
//        val hint = "Expand for details"
//
//        val builder = NotificationCompat.Builder(this, activeChannelId)
//            .setSmallIcon(R.drawable.outline_notifications_active_24)
//            .setContentTitle(title)
//            .setContentText("System status update... ($hint)") // Hardcoded hint in body for 100% visibility
//            .setSubText(hint) // Standard hint area
//            .setStyle(NotificationCompat.BigTextStyle()
//                .bigText(body)
//                .setBigContentTitle("Full Diagnostic Report")
//                .setSummaryText(hint)) // Expanded hint area
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//            .setAutoCancel(true)
//
//        val notificationManager = NotificationManagerCompat.from(this)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            notificationManager.notify(8888, builder.build())
//        }
//    }

}