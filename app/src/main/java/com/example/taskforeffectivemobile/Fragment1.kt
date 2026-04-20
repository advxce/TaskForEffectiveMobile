package com.example.taskforeffectivemobile

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.taskforeffectivemobile.databinding.Fragment1Binding
import com.example.taskforeffectivemobile.simularLocation.AndroidGPSLocation
import com.example.taskforeffectivemobile.simularLocation.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Fragment1 : BaseFragment() {

    private lateinit var binding: Fragment1Binding

    private lateinit var locationProvider: AndroidGPSLocation

    private var isCollecting = false
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            Toast.makeText(requireContext(), "Разрешения получены", Toast.LENGTH_SHORT).show()
            startLocationUpdates()
        } else {
            Toast.makeText(requireContext(), "Разрешения не получены", Toast.LENGTH_SHORT).show()
            binding.tvLocation.text = "Нет разрешения на геолокацию"
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationProvider = AndroidGPSLocation(requireContext().applicationContext)


        binding.textView.text = "Fragment1 - Тест геолокации"
        binding.tvLocation.text = "Ожидание геолокации..."

        binding.btnRequestPermissions.setOnClickListener {
            requestLocationPermissions()
        }

        binding.btnStartLocation.setOnClickListener {
            startLocationUpdates()
        }

        binding.btnStopLocation.setOnClickListener {
            stopLocationUpdates()
        }
    }

    private fun requestLocationPermissions() {
        val permissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else {
            Toast.makeText(requireContext(), "Разрешения уже есть", Toast.LENGTH_SHORT).show()
            startLocationUpdates()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun startLocationUpdates() {
        if (isCollecting) {
            Toast.makeText(requireContext(), "Уже подписаны", Toast.LENGTH_SHORT).show()
            return
        }

        if (!hasLocationPermission()) {
            Toast.makeText(requireContext(), "Сначала запросите разрешения", Toast.LENGTH_SHORT)
                .show()
            return
        }

        isCollecting = true
        binding.tvLocation.text = "Запуск геолокации..."

        lifecycleScope.launch {
            try {
                locationProvider.startBackgroundUpdates()
            } catch (e: SecurityException) {
                withContext(Dispatchers.Main) {
                    binding.tvLocation.text = "❌ Ошибка: нет разрешения"
                    Toast.makeText(requireContext(), "Нет разрешения на геолокацию", Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launch {
            locationProvider.getLocation().collect { result ->
                result.onSuccess { point ->
                    binding.tvLocation.text = """
                        📍 Координаты получены:
                        
                        Широта: ${point.latitude}
                        Долгота: ${point.longitude}
                        
                        Время: ${
                        java.text.SimpleDateFormat(
                            "HH:mm:ss",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date())
                    }
                    """.trimIndent()

                    binding.tvLocation.setBackgroundColor(
                        android.graphics.Color.parseColor("#E8F5E9")
                    )
                }.onFailure { error ->
                    binding.tvLocation.text = """
                        ❌ Ошибка геолокации:
                        
                        ${error.message}
                        
                        Используется дефолтная локация (Уфа)
                    """.trimIndent()
                    binding.tvLocation.setBackgroundColor(
                        android.graphics.Color.parseColor("#FFEBEE")
                    )
                }
            }
        }

        Toast.makeText(requireContext(), "Подписка на геолокацию запущена", Toast.LENGTH_SHORT)
            .show()
    }


    private fun stopLocationUpdates() {
        if (!isCollecting) {
            Toast.makeText(requireContext(), "Нет активной подписки", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            locationProvider.stopBackgroundUpdates()
        }

        isCollecting = false
        binding.tvLocation.text = "Геолокация остановлена"
        binding.tvLocation.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        Toast.makeText(requireContext(), "Подписка остановлена", Toast.LENGTH_SHORT).show()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isCollecting) {
            lifecycleScope.launch {
                locationProvider.stopBackgroundUpdates()
            }
        }
    }

    override fun getNextButton(): Button {
        return binding.button
    }

    override fun getPreviousButton(): Button {
        return binding.button2
    }

}