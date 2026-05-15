package com.example.jalsanchay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.jalsanchay.databinding.ActivityDataEntryBinding

class DataEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataEntryBinding
    private val viewModel: RainfallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("jal_prefs", MODE_PRIVATE)
        val roofArea = prefs.getFloat("roof_area", 0f).toDouble()

        if (roofArea == 0.0) {
            binding.etRainfallMm.isEnabled = false
            binding.btnSaveEntry.isEnabled = false
            binding.tvPreview.text = getString(R.string.setup_required)
            binding.tvPreview.setTextColor(0xFFFF0000.toInt()) // Red
        }

        // Live preview as user types
        binding.etRainfallMm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (roofArea == 0.0) return

                val text = s.toString().trim()
                if (text.isEmpty()) {
                    binding.tvPreview.text = getString(R.string.preview_default_text)
                    return
                }
                val mm = text.toDoubleOrNull()
                if (mm == null || mm < 0) {
                    binding.tvPreview.text = getString(R.string.error_invalid_number)
                    return
                }
                val liters = viewModel.calculateLiters(roofArea, mm)
                val days = viewModel.litersToWaterDays(liters)
                binding.tvPreview.text = getString(R.string.preview_format, liters, days)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnSaveEntry.setOnClickListener {
            val text = binding.etRainfallMm.text.toString().trim()

            // Input validation
            if (text.isEmpty()) {
                binding.etRainfallMm.error = getString(R.string.error_empty_rainfall)
                return@setOnClickListener
            }

            val mm = text.toDoubleOrNull()
            if (mm == null || mm < 0) {
                binding.etRainfallMm.error = getString(R.string.error_positive_number)
                return@setOnClickListener
            }

            if (roofArea == 0.0) {
                Toast.makeText(this, getString(R.string.setup_required), Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            viewModel.insertRecord(mm, roofArea)
            Toast.makeText(this, getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
