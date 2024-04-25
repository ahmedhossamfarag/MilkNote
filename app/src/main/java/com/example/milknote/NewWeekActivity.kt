package com.example.milknote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.milknote.databinding.ActivityNewWeekBinding
import com.example.milknote.databinding.NamesTableItemBinding
import java.util.Calendar

class NewWeekActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewWeekBinding
    private lateinit var names: ArrayList<String>
    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewWeekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addName.setOnClickListener { addNew() }

        binding.create.setOnClickListener { createWeek() }

        names = ArrayList<String>().also { it.addAll(Shared.database?.getNames()!!) }

        database = Shared.database!!

        addNames()
    }


    private fun addNames() {
        for (name in names){
            addRow(name)
        }
    }

    private fun addNew() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.str_name)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(input)

        builder.setPositiveButton(R.string.str_ok
        ) { _, _ -> addNew(input.text.toString())}
        builder.setNegativeButton(R.string.str_cancel
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun addNew(name: String) {
        if(name.any()){
            if(!names.contains(name)){
                names.add(name)
                database.addName(name)
                addRow(name)
            }
        }
    }

    private fun addRow(name: String) {
        val row = NamesTableItemBinding.inflate(layoutInflater)
        row.nameText.text = name
        row.deleteName.setOnClickListener{
                deleteName(row, name)
        }
        binding.namesTable.addView(row.root)
    }

    private fun deleteName(row: NamesTableItemBinding, name: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.sure)

        val text = TextView(this)
        text.text = name

        builder.setView(text)

        builder.setPositiveButton(R.string.str_ok
        ) { _, _ ->
            binding.namesTable.removeView(row.root)
            database.deleteName(name)
            names.remove(name)
        }
        builder.setNegativeButton(R.string.str_cancel
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
    }


    private fun createWeek() {
        val calender = Calendar.getInstance()
        val week = "${calender.get(Calendar.YEAR)}_${calender.get(Calendar.MONTH)}_${calender.get(Calendar.DAY_OF_MONTH)}"
        database.createWeek(week)
        startActivity(Intent(this, MainActivity::class.java).also { it.putExtra("week", week) })
    }
}