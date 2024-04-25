package com.example.milknote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.CheckBox
import android.widget.TableRow
import android.widget.TextView
import com.example.milknote.databinding.ActivityRecordsViewBinding

class RecordsViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordsViewBinding
    private lateinit var records: Array<Pair<String, Double>>
    private var unit: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        unit = intent.extras?.getDouble("unit") ?: 0.0
        records = Shared.records!!
        addRecords()
    }

    private fun addRecords() {
        for (record in records){
            val row = TableRow(this)
            for (str in arrayOf(record.first, record.second.toString(), (record.second * unit).toString()))
                row.addView(TextView(this).also { it.text = str; it.gravity = Gravity.CENTER; it.textSize = 14f })
            row.addView(CheckBox(this))
            binding.recordTable.addView(row)
        }
    }
}