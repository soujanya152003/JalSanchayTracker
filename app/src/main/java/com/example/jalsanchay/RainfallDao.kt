package com.example.jalsanchay

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RainfallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RainfallRecord)

    @Query("SELECT * FROM rainfall_records ORDER BY date DESC")
    fun getAllRecords(): LiveData<List<RainfallRecord>>

    @Query("SELECT SUM(litersHarvested) FROM rainfall_records")
    fun getTotalLiters(): LiveData<Double?>

    @Query("SELECT SUM(litersHarvested) FROM rainfall_records WHERE date = :today")
    fun getTodayLiters(today: String): LiveData<Double?>

    @Query("SELECT month, SUM(litersHarvested) as total FROM rainfall_records GROUP BY month ORDER BY month DESC")
    fun getMonthlyReport(): LiveData<List<MonthlyData>>

    @Delete
    suspend fun deleteRecord(record: RainfallRecord)
}
