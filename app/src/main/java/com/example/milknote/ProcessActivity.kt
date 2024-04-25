package com.example.milknote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.milknote.databinding.ActivityProcessBinding
import com.example.milknote.databinding.CustomerCutDialogBinding
import com.example.milknote.databinding.CustomerTableItemBinding
import com.google.android.material.snackbar.Snackbar

class ProcessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProcessBinding
    private lateinit var customers: MutableList<Pair<String, Double>>
    private lateinit var sumList: MutableList<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customers = mutableListOf<Pair<String, Double>>()
            .also { it.addAll(Shared.customers?.map { c -> c.name to c.values.sumOf{d -> d ?: 0.0} }!!) }

        sumList = mutableListOf()

        addCustomers()

        binding.createRocords.setOnClickListener { createRecords() }
    }


    private fun addCustomers() {
        for (i in customers.indices)
            addCustomer(customers[i], i)
    }

    private fun addCustomer(pair: Pair<String, Double>, i: Int) {
        val row = CustomerTableItemBinding.inflate(layoutInflater)
        row.customerName.text = pair.first
        row.customerSum.text = pair.second.toString()
        row.customerCut.setOnClickListener{cut(i)}
        sumList.add(i, row.customerSum)
        binding.customersTable.addView(row.root, i)
    }

    private fun cut(i: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.str_name)

        val input = CustomerCutDialogBinding.inflate(layoutInflater)

        builder.setView(input.root)

        builder.setPositiveButton(R.string.str_ok
        ) { _, _ -> cut(i, input.cutName.text.toString(), input.cutValue.text.toString().toDoubleOrNull() ?: 0.0)}
        builder.setNegativeButton(R.string.str_cancel
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun cut(i: Int, name: String, value: Double) {
        val src = customers[i]
        if(value < src.second)
        {
            customers[i] = src.first to (src.second - value)
            customers.add(i+1, name to value)
            sumList[i].text = customers[i].second.toString()
            addCustomer(customers[i+1], i+1)
        }
        else
            Snackbar.make(binding.root, R.string.invalid_cut, Snackbar.LENGTH_LONG).show()
    }

    private fun createRecords() {
        Shared.records = customers.toTypedArray()
        val unit = binding.unitEdit.text.toString().toDoubleOrNull() ?: 0.0
        startActivity(Intent(this, RecordsViewActivity::class.java).also { it.putExtra("unit", unit) })
    }
}