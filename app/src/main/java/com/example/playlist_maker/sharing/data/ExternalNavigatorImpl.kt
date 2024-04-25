package com.example.playlist_maker.sharing.data

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.example.playlist_maker.R


class ExternalNavigatorImpl(private val context: Context):ExternalNavigator {


    override fun shareLink() {
        context.startActivity(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.course_link))
                type = "text/plain"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, null
        )
    }


    override fun openLink() {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.offer_link)
             )
           ).setFlags(FLAG_ACTIVITY_NEW_TASK)
        )

    }


    override fun openEmail() {
        context.startActivity(
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_text))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email_recipient))
                )
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

}