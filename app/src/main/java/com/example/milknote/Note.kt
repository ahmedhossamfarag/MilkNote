package com.example.milknote

import android.text.InputType
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.milknote.databinding.NoteBinding

class Note(private val customer: Customer, private val app: AppCompatActivity, private val database: Database) {
    private var _binding: NoteBinding? = null

    private val binding get() = _binding!!

    val view get() = binding.root

    init {
        _binding = NoteBinding.inflate(app.layoutInflater)
        setName()
        setEntities()
    }

    private fun setName(){
        val nameView = view.getChildAt(0) as TextView
        nameView.text = customer.name
    }

    private fun getEntities() : Array<TextView>{
        val entities = ArrayList<TextView>()
        val table = view.getChildAt(1) as TableLayout
        for (row in table.children)
            for(tv in (row as TableRow).children)
                entities.add(tv as TextView)
        return entities.toTypedArray()
    }

    private fun setEntities() {
        val entities = getEntities()
        for(i in entities.indices)
        {
            entities[i].text = customer.values[i]?.toString() ?: ""
            entities[i].setOnClickListener {
                edit(entities[i], i)
            }
        }
    }

    private fun edit(textView: TextView, i: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(app)
        builder.setTitle(R.string.str_value)

        val input = EditText(app)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        customer.values[i]?.let { input.text.append(it.toString()) }

        builder.setView(input)

        builder.setPositiveButton(R.string.str_ok
        ) { _, _ -> setValue(textView, i, input.text.toString().toDoubleOrNull())}

        builder.setNegativeButton(R.string.str_cancel
        ) { dialog, _ -> dialog.cancel() }

        builder.show()

    }

    private fun setValue(textView: TextView, i: Int, value: Double?) {
        textView.text = value?.toString() ?: ""
        customer.values[i] = value
        database.setValue(customer.week, customer.name, i, value)
    }
}