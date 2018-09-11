package com.naledimadlopha.gdgbot.app.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.naledimadlopha.gdgbot.app.R
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var adapter: MessageListAdapter = MessageListAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageListRecyclerView.adapter = adapter
    }

}
