package pro.udeedit.devtools.pushestest.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pro.udeedit.devtools.anarchist.AnarchistPermissionStatus
import pro.udeedit.devtools.anarchist.AnarchistPermissionUtils
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.databinding.ActivityMainBinding
import pro.udeedit.devtools.pushestest.utils.CHANNEL_ID
import pro.udeedit.devtools.pushestest.utils.CHANNEL_NAME
import pro.udeedit.devtools.pushestest.utils.DEFAULT_NOTIF_ID
import pro.udeedit.devtools.pushestest.utils.REQUEST_PERMISSION_CODE

private const val TAG = "MainActivity"

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

    // text watcher
    lateinit var textWatcherTitle: TextWatcher
    lateinit var textWatcherBody: TextWatcher
    
    var isRecreated = false

    // binding
    private lateinit var binding: ActivityMainBinding


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // setup window view
        setupWindow()
        createVibratorObject()
        createNotificationsChannel()

        if (savedInstanceState != null) {
            // activity recreated (configuration change, theme switch, etc.)
            isRecreated = true

        }  else {
            // first launch
            createTextWatcherTitleObject()
            createTextWatcherBodyObject()

            binding.edtNotificationTitle.addTextChangedListener(textWatcherTitle)
            binding.edtNotificationBody.addTextChangedListener(textWatcherBody)
        }

        binding.btnSendNotification.setOnClickListener {
            onSendNotification()
        }
    }

    override fun onResume() {
        super.onResume()
        initializePermissionList()
        requestSinglePermission()
    }

    private fun setupWindow() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    /** Set text watcher object for notification title */
    fun createTextWatcherTitleObject() {
        textWatcherTitle = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    setErrorNotificationTitle(true)

                } else {
                    setErrorNotificationTitle(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
    }

    /** Set text watcher object for notification body */
    fun createTextWatcherBodyObject() {
        textWatcherBody = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    setErrorNotificationBody(true)

                } else {
                    setErrorNotificationBody(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
    }

    /** setup notification channel for sending notification within the app */
    private fun createNotificationsChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /** check if notification fields are correct ans send notification */
    private fun onSendNotification() {
        val notificationTitle = binding.edtNotificationTitle.text.toString()
        val notificationBody = binding.edtNotificationBody.text.toString()

        if (notificationTitle.isBlank()) {
            setErrorNotificationTitle(true)
        }

        if (notificationBody.isBlank()) {
            setErrorNotificationBody(true)
        }

        if (notificationTitle.isBlank() || notificationBody.isBlank()) {
            return
        }

        publishNotification(notificationTitle, notificationBody)
    }

    /** publish actual notification */
    private fun publishNotification(title: String, body: String) {
        val intent=Intent(this, MainActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_alarm_bell)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notifManger = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notifManger.notify(DEFAULT_NOTIF_ID, notif)
            vibrateSuccess()
        }
    }

    /** set error to notification title field, if there is no text entered */
    private fun setErrorNotificationTitle(isError: Boolean) {
        if (isError) {
            binding.tilNotificationTitle.isErrorEnabled = true
            binding.tilNotificationTitle.error = getString(R.string.please_fill_notification_title)
            vibrateError()

        } else {
            binding.tilNotificationTitle.isErrorEnabled = false
            binding.tilNotificationTitle.error = null
        }
    }

    /** set error to notification body field, if there is no text entered */
    private fun setErrorNotificationBody(isError: Boolean) {
        if (isError) {
            binding.tilNotificationBody.isErrorEnabled = true
            binding.tilNotificationBody.error = getString(R.string.please_fill_notification_text)
            vibrateError()

        } else {
            binding.tilNotificationBody.isErrorEnabled = false
            binding.tilNotificationBody.error = null
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrateError() {
        Log.d(TAG, "vibrateError")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(patternError, -1))

        } else {
            vibrator.vibrate(vibrationDurationError)
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrateSuccess() {
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
}