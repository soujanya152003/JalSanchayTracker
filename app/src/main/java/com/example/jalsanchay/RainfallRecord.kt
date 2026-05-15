package com.example.jalsanchay

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rainfall_records")
data class RainfallRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,          // Format: YYYY-MM-DD
    val rainfallMm: Double,    // Rainfall in millimeters
    val roofAreaSqFt: Double,  // Roof area in square feet
    val litersHarvested: Double, // Calculated liters
    val month: String          // Format: YYYY-MM (for monthly report)
)
