package com.example.jalsanchay

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.jalsanchay.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("jal_prefs", MODE_PRIVATE)

        // Load saved values
        val savedRoof = prefs.getFloat("roof_area", 0f)
        val savedTank = prefs.getFloat("tank_capacity", 0f)
        if (savedRoof > 0) binding.etRoofArea.setText(savedRoof.toString())
        if (savedTank > 0) binding.etTankCapacity.setText(savedTank.toString())

        binding.btnSaveSetup.setOnClickListener {
            val roofText = binding.etRoofArea.text.toString().trim()
            val tankText = binding.etTankCapacity.text.toString().trim()

            // Input validation
            if (roofText.isEmpty() || tankText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val roofArea = roofText.toDoubleOrNull()
            val tankCapacity = tankText.toDoubleOrNull()

            if (roofArea == null || roofArea <= 0) {
                binding.etRoofArea.error = "Please enter a valid roof area"
                return@setOnClickListener
            }

            if (tankCapacity == null || tankCapacity <= 0) {
                binding.etTankCapacity.error = "Please enter a valid tank capacity"
                return@setOnClickListener
            }

            // Save to SharedPreferences using KTX extension
            prefs.edit {
                putFloat("roof_area", roofArea.toFloat())
                putFloat("tank_capacity", tankCapacity.toFloat())
            }

            Toast.makeText(this, "Setup Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
