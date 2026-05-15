package com.example.jalsanchay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jalsanchay.databinding.ActivityReportBinding
import com.example.jalsanchay.databinding.ItemMonthlyReportBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private val viewModel: RainfallViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = MonthlyReportAdapter()
        binding.recyclerViewReport.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewReport.adapter = adapter

        viewModel.monthlyReport.observe(this) { data ->
            adapter.submitList(data)
        }
    }
}

class MonthlyReportAdapter : ListAdapter<MonthlyData, MonthlyReportAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(val binding: ItemMonthlyReportBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMonthlyReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        
        // Format YYYY-MM to Month YYYY (e.g., 2023-10 to October 2023)
        val displayMonth = try {
            val inputFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(item.month)
            date?.let { outputFormat.format(it) } ?: item.month
        } catch (e: Exception) {
            item.month
        }

        holder.binding.tvMonth.text = displayMonth
        holder.binding.tvMonthTotal.text = holder.itemView.context.getString(R.string.liters_format, item.total)
    }

    class DiffCallback : DiffUtil.ItemCallback<MonthlyData>() {
        override fun areItemsTheSame(oldItem: MonthlyData, newItem: MonthlyData): Boolean {
            return oldItem.month == newItem.month
        }

        override fun areContentsTheSame(oldItem: MonthlyData, newItem: MonthlyData): Boolean {
            return oldItem == newItem
        }
    }
}
