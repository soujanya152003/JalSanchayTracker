package com.example.jalsanchay

import androidx.lifecycle.LiveData

class RainfallRepository(private val dao: RainfallDao) {

    val allRecords: LiveData<List<RainfallRecord>> = dao.getAllRecords()
    val totalLiters: LiveData<Double?> = dao.getTotalLiters()
    val monthlyReport: LiveData<List<MonthlyData>> = dao.getMonthlyReport()

    fun getTodayLiters(today: String): LiveData<Double?> = dao.getTodayLiters(today)

    suspend fun insert(record: RainfallRecord) {
        dao.insertRecord(record)
    }

    suspend fun delete(record: RainfallRecord) {
        dao.deleteRecord(record)
    }
}
