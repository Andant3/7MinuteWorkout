package com.example.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.example.a7minuteworkout.databinding.ActivityFinishBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.Inflater

class FinishActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolBarFinishActivity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        binding.toolBarFinishActivity.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnFinish.setOnClickListener{
            finish()
        }
        binding.llFinish.setOnClickListener{
            finish()
        }

        addDateToDataBase()
    }

    private fun addDateToDataBase(){
        val calendar = Calendar.getInstance()
        val dateTime = calendar.time
        Log.i("DATE", "" + dateTime)

        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        val date = sdf.format(dateTime)

        val dbHandler = SqliteOpenHelper(this, null)
        dbHandler.addDate(date)
        Log.i("DATE: ", "Added")
    }
}