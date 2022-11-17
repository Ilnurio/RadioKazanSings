package com.example.radiokazansings

import android.content.Context
import android.content.Intent

fun share(context: Context){
    val sharingIntent = Intent(Intent.ACTION_SEND)
    // type of the content to be shared
    sharingIntent.type = "text/plain"
    // Body of the content
    val shareBody = context.getString(R.string.share_body)
    // subject of the content. you can share anything
    val shareSubject = context.getString(R.string.share_googleplay_link)
    // passing body of the content
    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
    // passing subject of the content
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject)
    context.startActivity(Intent.createChooser(sharingIntent, "Share using"))
}