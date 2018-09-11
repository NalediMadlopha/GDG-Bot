package com.naledimadlopha.gdgbot.app.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.naledimadlopha.gdgbot.app.R
import com.naledimadlopha.gdgbot.app.model.BaseMessage
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.message_received_item.*

class MessageListAdapter(private var messageList: MutableList<BaseMessage>) : RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_sent_item, parent, false)
            MessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_received_item, parent, false)
            MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        viewHolder.bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        return if (message.sender == SELF) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    fun addMessage(message: BaseMessage) {
        this.messageList.add(message)
        notifyDataSetChanged()
    }

    class MessageViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(chatMessage: BaseMessage) {
            messageBodyTextView.text = chatMessage.message
            messageTimeStampTextView.text = chatMessage.timeStamp
        }

    }

    companion object {
        const val VIEW_TYPE_MESSAGE_SENT = 1
        const val VIEW_TYPE_MESSAGE_RECEIVED = 2

        const val SELF = "self"
    }
}
