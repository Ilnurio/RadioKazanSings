package com.example.radiokazansings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.radiokazansings.databinding.ActivityContactsBinding
import com.example.radiokazansings.databinding.ActivityMainBinding

class Contacts : AppCompatActivity() {
    lateinit var binding: ActivityContactsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    fun onClickGoMain(view: View){
        finish()
    }
}