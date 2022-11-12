package com.example.radiokazansings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import com.example.radiokazansings.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity() {
    lateinit var binding: ActivityContactsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linkTextView = binding.tvLink
        val linkEmail = binding.tvEmail
        val linkVK = binding.tvVK
        val linkTG = binding.tvTG
        val linkWhatsApp = binding.tvWhatsapp

        linkTextView.movementMethod = LinkMovementMethod.getInstance()
        linkEmail.movementMethod = LinkMovementMethod.getInstance()
        linkVK.movementMethod = LinkMovementMethod.getInstance()
        linkTG.movementMethod = LinkMovementMethod.getInstance()
        linkWhatsApp.movementMethod = LinkMovementMethod.getInstance()

    }
    fun onClickGoMain(view: View){
        finish()
    }
}