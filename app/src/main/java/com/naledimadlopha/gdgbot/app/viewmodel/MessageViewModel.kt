package com.naledimadlopha.gdgbot.app.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.AsyncTask
import com.naledimadlopha.gdgbot.app.model.BaseMessage
import com.naledimadlopha.gdgbot.app.repository.MessageRepository
import com.naledimadlopha.gdgbot.app.view.MessageView
import java.text.SimpleDateFormat
import java.util.*

class MessageViewModel(private var view: MessageView) : ViewModel() {

    fun postMessage(message: String) {
        view.updateMessages(BaseMessage(message, SELF, simpleDateFormat.format(Date())))
        PostMessageTask(view, MessageRepository()).execute(message, "en", "12345")
    }

    private class PostMessageTask(val view: MessageView, val repository: MessageRepository) : AsyncTask<String, Void, BaseMessage>() {

        override fun doInBackground(vararg params: String): BaseMessage {
            val response = repository.postMessage(params[0], params[1], params[2])
            val body = response.body()!!
            val result = body.getAsJsonObject(RESULT)
            val source = result.get(SOURCE).asString
            val speech = result.get(SPEECH).asString

            return BaseMessage(speech, source, simpleDateFormat.format(Date()))
        }

        override fun onPostExecute(result: BaseMessage) {
            super.onPostExecute(result)
            view.updateMessages(result)
        }

    }

    companion object {
        private const val RESULT = "result"
        private const val SPEECH = "speech"
        private const val SOURCE = "source"
        private const val SELF = "self"
        private const val TIME_FORMAT = "HH:mm"
        private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    }

}
