package pro.udeedit.devtools.pushestest.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
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

    private lateinit var vibrator: Vibrator
    val requestPermissionList: MutableList<String> = mutableListOf()
    private lateinit var binding: ActivityMainBinding

    val patternConfirmation = longArrayOf(0, 100, 50, 100) // pattern: wait, vibrate, wait, vibrate
    // Pattern: vibrate-beep pattern (longer, more aggressive)
    val patternError = longArrayOf(0, 300, 100, 300, 100, 300) // Vibrate 3 times with pauses
    private val vibrationDurationSuccess = 500L
    private val vibrationDurationError = 1000L


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android S (API level 31) and above
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
//            val vibratorManager = getSystemService<VibratorManager>()
            vibrator = vibratorManager.defaultVibrator // getDefaultVibrator()
//            vibrator.vibrate(VibrationEffect.createOneShot(vibrationDuration, VibrationEffect.DEFAULT_AMPLITUDE))

        } else {
            // For earlier versions
            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            vibrator.vibrate(vibrationDuration) // Deprecated vibrate function
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
//            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
//
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//
//                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
//                view.setBackgroundColor(getColor(R.color.push_color_variant))
//
//                // Adjust padding to avoid overlap
//                view.setPadding(0, statusBarInsets.top, 0, 0)
//                insets
//            }
//
//        } else {
//            // For Android 14 and below
//            window.statusBarColor = getColor(R.color.push_color_variant)
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val decorView = window.decorView
//            if (isDarkThemeActive()) {
//                window.statusBarColor = getColor(R.color.push_color_variant) // dark mode background
//                decorView.systemUiVisibility = decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
//            } else {
//                window.statusBarColor = getColor(R.color.push_color_variant) // light mode background
//                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//        }
////        if (Build.VERSION.SDK_INT >= 29) {
//            val window = this.window
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
////            window.statusBarColor = this.resources.getColor(R.color.push_color_variant)
////        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
//            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
//                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
//                view.setBackgroundColor(this.resources.getColor(R.color.push_color_variant))
//
//                // Adjust padding to avoid overlap
////                view.setPadding(0, statusBarInsets.top, 0, 0)
//                view.setPadding(statusBarInsets.left, statusBarInsets.top, statusBarInsets.right, statusBarInsets.bottom)
//                insets
//            }
//
//        } else {
//            // For Android 14 and below
//            window.statusBarColor = this.resources.getColor(R.color.push_color_variant)
//        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // For dark mode
//            // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // For light mode
//        }

        createNotificationsChannel()

        binding.edtNotificationTitle.addTextChangedListener(object : TextWatcher {
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
        })

        binding.edtNotificationBody.addTextChangedListener(object : TextWatcher {
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
        })

        binding.btnSendNotification.setOnClickListener {
            onSendNotification()
        }
    }

    fun isDarkThemeActive(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

//    fun setStatusBarColor(window: Window, color: Int) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
//            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
//                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
//                view.setBackgroundColor(color)
//
//                // Adjust padding to avoid overlap
//                view.setPadding(0, statusBarInsets.top, 0, 0)
//                insets
//            }
//        } else {
//            // For Android 14 and below
//            window.statusBarColor = color
//        }
//    }

    override fun onResume() {
        super.onResume()
        initializePermissionList()
        requestSinglePermission()
    }

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


    private fun onSendNotification() {
        val notificationTitle = binding.edtNotificationTitle.text.toString()
        val notificationBody = binding.edtNotificationBody.text.toString()
        Log.d(TAG, "notificationTitle = $notificationTitle, isEmpty = ${notificationTitle.isEmpty()}")

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

    private fun vibrateError() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val vibrationEffect = VibrationEffect.createOneShot(vibrationDuration, VibrationEffect.DEFAULT_AMPLITUDE)
//            vibrator.vibrate(vibrationEffect)
            vibrator.vibrate(VibrationEffect.createWaveform(patternError, -1))

        } else {
            vibrator.vibrate(vibrationDurationError)
        }
    }

    private fun vibrateSuccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val vibrationEffect = VibrationEffect.createOneShot(vibrationDuration, VibrationEffect.DEFAULT_AMPLITUDE)
//            vibrator.vibrate(vibrationEffect)
            vibrator.vibrate(VibrationEffect.createWaveform(patternConfirmation, -1))

        } else {
            vibrator.vibrate(vibrationDurationSuccess)
        }
    }

    private fun publishNotification(title: String, body: String) {
        val intent=Intent(this, MainActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(title) // "Sample Title"
            .setContentText(body) // "This is sample body notif"
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


    private fun initializePermissionList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionList.add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun requestSinglePermission() {
        val permissionResult = AnarchistPermissionUtils.checkAndRequestPermissions(
                this,
                requestPermissionList,
                REQUEST_PERMISSION_CODE
            )

        when (permissionResult.finalStatus) {
            AnarchistPermissionStatus.ALLOWED -> {
                Toast.makeText(
                    this,
                    getString(R.string.permission_is_already_allowed),
                    Toast.LENGTH_LONG
                ).show()
            }

            AnarchistPermissionStatus.DENIED_PERMANENTLY -> {
                //Request user to allow permission by sending to permission list page
                //You can show customized dialog and then call this function
                Toast.makeText(this, getString(R.string.permission_is_permanently_denied), Toast.LENGTH_LONG)
                    .show()
                AnarchistPermissionUtils.askUserToRequestPermissionExplicitly(this)
            }

            else -> {
                //Permission is requesting for first time or user denied permission before but not permanently
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
            //Check status after user allowed or denied permission using the same way while requested permission
            val permissionResult = AnarchistPermissionUtils.checkAndRequestPermissions(
                    this,
                    requestPermissionList,
                    REQUEST_PERMISSION_CODE,
                    checkStatusOnly = true
                )

            when (permissionResult.finalStatus) {
                AnarchistPermissionStatus.ALLOWED -> {//DO further stuffs as all permissions are allowed by user
                    Toast.makeText(this, getString(R.string.permission_allowed), Toast.LENGTH_LONG).show()
                }

                AnarchistPermissionStatus.DENIED_PERMANENTLY -> {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_is_permanently_denied),
                        Toast.LENGTH_LONG
                    ).show()

                } else -> {
                    //Permission denied by user but not permanently
                }
            }
        }
    }
}