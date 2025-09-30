package pro.udeedit.devtools.pushestest.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pro.udeedit.devtools.anarchist.AnarchistPermissionStatus
import pro.udeedit.devtools.anarchist.AnarchistPermissionUtils
import pro.udeedit.devtools.pushestest.R
import pro.udeedit.devtools.pushestest.databinding.ActivityMainBinding
import pro.udeedit.devtools.pushestest.utils.CHANNEL_ID
import pro.udeedit.devtools.pushestest.utils.CHANNEL_NAME
import pro.udeedit.devtools.pushestest.utils.NOTIF_ID

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CODE = 12

    val requestPermissionList: MutableList<String> = mutableListOf()
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // For dark mode
            // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // For light mode
        }

        createNotificationsChannel()

        binding.btnSendNotification.setOnClickListener {
            publishNotification()
        }
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

    private fun publishNotification() {
        val intent=Intent(this, MainActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Sample Title")
            .setContentText("This is sample body notif")
            .setSmallIcon(R.drawable.ic_alarm_bell)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notifManger = NotificationManagerCompat.from(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notifManger.notify(NOTIF_ID, notif)
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