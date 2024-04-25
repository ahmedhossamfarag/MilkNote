package com.example.milknote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.milknote.databinding.ActivityReviewBinding

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private lateinit var weeks: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weeks = Shared.database?.getWeeks()!!
        binding.weeksList.adapter = ArrayAdapter(this, R.layout.week_list_item, weeks)
        binding.weeksList.setOnItemClickListener{
                _,_,i,_ -> startActivity(Intent(this, MainActivity::class.java).also { it.putExtra("week", weeks[i]) })

        }
    }
}