package com.example.taskforeffectivemobile

import android.os.Build
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class MainActivity : AppCompatActivity(), Router{

    private var currentScreen = Screen.SCREEN_1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        navigationManager(currentScreen)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<ChargingNotifyWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)

    }

    override fun navigateToNextScreen() {
       when(currentScreen){
            Screen.SCREEN_1 -> {
                currentScreen = Screen.SCREEN_2
                replaceFragment(Fragment2())
            }
            Screen.SCREEN_2 -> {
                currentScreen = Screen.SCREEN_3
                replaceFragment(Fragment3())
            }
            Screen.SCREEN_3 -> {
                currentScreen = Screen.SCREEN_1
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                replaceFragment(Fragment1())
            }
        }
        println("currentScreen $currentScreen")
    }

    override fun navigateToPreviousScreen() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun navigationManager(screen: Screen) {

        val currentFragment = when(screen){
            Screen.SCREEN_1 -> Fragment1()
            Screen.SCREEN_2 -> Fragment2()
            Screen.SCREEN_3 -> Fragment3()
        }

        replaceFragment(currentFragment)
        currentScreen = screen
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}