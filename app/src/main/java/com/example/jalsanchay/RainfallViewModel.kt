package com.example.jalsanchay

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RainfallViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RainfallRepository
    val allRecords: LiveData<List<RainfallRecord>>
    val totalLiters: LiveData<Double?>
    val monthlyReport: LiveData<List<MonthlyData>>

    init {
        val dao = AppDatabase.getDatabase(application).rainfallDao()
        repository = RainfallRepository(dao)
        allRecords = repository.allRecords
        totalLiters = repository.totalLiters
        monthlyReport = repository.monthlyReport
    }

    fun getTodayLiters(today: String) = repository.getTodayLiters(today)

    // MAIN FORMULA: Liters = Area x Rainfall x 0.0929 x RunoffCoefficient
    fun calculateLiters(roofAreaSqFt: Double, rainfallMm: Double): Double {
        val runoffCoefficient = 0.85
        return roofAreaSqFt * rainfallMm * 0.0929 * runoffCoefficient
    }

    // Converts liters to household water days (avg 150 liters/day per household)
    fun litersToWaterDays(liters: Double): Double {
        return liters / 150.0
    }

    fun insertRecord(rainfallMm: Double, roofAreaSqFt: Double) {
        val liters = calculateLiters(roofAreaSqFt, rainfallMm)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val now = Date()
        val record = RainfallRecord(
            date = dateFormat.format(now),
            rainfallMm = rainfallMm,
            roofAreaSqFt = roofAreaSqFt,
            litersHarvested = liters,
            month = monthFormat.format(now)
        )
        viewModelScope.launch {
            repository.insert(record)
        }
    }

    fun deleteRecord(record: RainfallRecord) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }
}
