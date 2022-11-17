package com.example.radiokazansings

import android.content.Intent
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.radiokazansings.databinding.ActivityInformationBinding


class InformationActivity : AppCompatActivity() {
    lateinit var binding: ActivityInformationBinding
    private lateinit var customToolbar: Toolbar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // textView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD

        customToolbar = binding.customToolbar2
        customToolbar.inflateMenu(R.menu.menu_for_information)
        customToolbar.setOnMenuItemClickListener{
            when (it.itemId){
                R.id.share2 -> {
                    share(this)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }
    fun onClickGoMainActivity(view: View){
        finish()
    }
}