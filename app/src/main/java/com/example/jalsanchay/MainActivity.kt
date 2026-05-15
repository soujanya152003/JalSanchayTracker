package com.example.jalsanchay

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.jalsanchay.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RainfallViewModel by viewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("jal_prefs", MODE_PRIVATE)

        // If no setup done, go to setup screen first
        if (prefs.getFloat("roof_area", 0f) == 0f) {
            startActivity(Intent(this, SetupActivity::class.java))
        }

        setupObservers()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        // Refresh tank progress when returning from Setup in case tank capacity changed
        updateTankProgress(viewModel.totalLiters.value ?: 0.0)
    }

    private fun setupObservers() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Observe today's liters
        viewModel.getTodayLiters(today).observe(this) { todayLiters ->
            val liters = todayLiters ?: 0.0
            binding.tvTodayLiters.text = getString(R.string.liters_format, liters)
        }

        // Observe total liters and update tank + impact score
        viewModel.totalLiters.observe(this) { total ->
            val totalLiters = total ?: 0.0
            binding.tvTotalLiters.text = getString(R.string.liters_format, totalLiters)
            updateTankProgress(totalLiters)

            // Update impact score
            val days = viewModel.litersToWaterDays(totalLiters)
            binding.tvImpactScore.text = getString(R.string.impact_score_format, days)
        }
    }

    private fun updateTankProgress(totalLiters: Double) {
        // Read latest tank capacity from prefs
        val tankCapacity = prefs.getFloat("tank_capacity", 5000f).toDouble()

        // Update tank progress bar
        val percent = if (tankCapacity > 0) {
            ((totalLiters / tankCapacity) * 100).toInt().coerceIn(0, 100)
        } else 0

        binding.progressBarTank.progress = percent
        binding.tvTankPercent.text = getString(R.string.tank_percent_format, percent)
    }

    private fun setupButtons() {
        binding.btnAddRainfall.setOnClickListener {
            startActivity(Intent(this, DataEntryActivity::class.java))
        }
        binding.btnSetup.setOnClickListener {
            startActivity(Intent(this, SetupActivity::class.java))
        }
        binding.btnReport.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
        binding.btnTips.setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
        }
    }
}
