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


class Information : AppCompatActivity() {
    lateinit var binding: ActivityInformationBinding
    private lateinit var customToolbar: Toolbar

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val textView = binding.information
        textView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD

        customToolbar = binding.customToolbar2
        customToolbar.inflateMenu(R.menu.bitrate_menu)
        customToolbar.setOnMenuItemClickListener{
            when (it.itemId){
                R.id.share -> {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    // type of the content to be shared
                    sharingIntent.type = "text/plain"
                    // Body of the content
                    val shareBody = getString(R.string.share_body)
                    // subject of the content. you can share anything
                    val shareSubject = R.drawable.logo_new
                    // passing body of the content
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                    // passing subject of the content
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                    startActivity(Intent.createChooser(sharingIntent, "Share using"))
                }
            }
            return@setOnMenuItemClickListener true
        }
    }
    fun onClickGoMainActivity(view: View){
        finish()
    }
}