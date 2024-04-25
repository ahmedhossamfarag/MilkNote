package com.example.milknote

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.milknote.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var database: Database
    private lateinit var customers: ArrayList<Customer>
    private lateinit var week: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.addNew.setOnClickListener {
            addNew()
        }

        database = Shared.database ?: Database(this)
        Shared.database = database

        addNotes()
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

    private fun addNew(name: String){
        if(name.any())
            if(!customers.any { it.name == name })
                database.addCustomer(week, name).apply { customers.add(this); addNote(this) }
            else
                Snackbar.make(binding.root, R.string.name_exists, Snackbar.LENGTH_LONG).show()
    }

    private fun addNotes() {

        week = intent.extras?.getString("week") ?: database.getWeeks().let { if(it.any()) it.last() else "" }
        if(week.any()){
            customers = ArrayList<Customer>().also { it.addAll(database.getWeek(week)) }
            for (customer in customers)
                addNote(customer)
        }
        else{
            binding.addNew.hide()
        }
    }

    private fun addNote(customer: Customer) {
        val note = Note(customer, this, database)
        binding.content.addView(note.view)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> startActivity(Intent(this, NewWeekActivity::class.java))
            R.id.action_review -> startActivity(Intent(this, ReviewActivity::class.java))
            R.id.action_process -> {
                Shared.customers = customers
                startActivity(Intent(this, ProcessActivity::class.java))
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

}