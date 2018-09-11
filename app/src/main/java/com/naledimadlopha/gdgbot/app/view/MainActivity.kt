package com.naledimadlopha.gdgbot.app.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.naledimadlopha.gdgbot.app.R
import com.naledimadlopha.gdgbot.app.model.BaseMessage
import com.naledimadlopha.gdgbot.app.view.MessageListAdapter.Companion.SELF
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val adapter: MessageListAdapter = MessageListAdapter(ArrayList())
    private lateinit var simpleDateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        simpleDateFormat = SimpleDateFormat(getString(R.string.message_time_format), Locale.getDefault())

        messageListRecyclerView.adapter = adapter
    }

    fun sendButtonClicked(view: View) {
        val message = messageEditorEditText.text.toString()

        if (!TextUtils.isEmpty(message)) {
            adapter.addMessage(BaseMessage(message, SELF, simpleDateFormat.format(Date())))
            messageEditorEditText.setText("")
        }
    }


}
