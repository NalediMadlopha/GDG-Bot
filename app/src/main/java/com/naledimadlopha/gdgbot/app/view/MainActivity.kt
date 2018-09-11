package com.naledimadlopha.gdgbot.app.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import com.naledimadlopha.gdgbot.app.R
import com.naledimadlopha.gdgbot.app.model.BaseMessage
import com.naledimadlopha.gdgbot.app.repository.MessageRepository
import com.naledimadlopha.gdgbot.app.view.MessageListAdapter.Companion.SELF
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), MessageView {

    private val adapter: MessageListAdapter = MessageListAdapter(ArrayList())
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent

    private lateinit var simpleDateFormat: SimpleDateFormat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()
        setupSpeechRecognizerIntent()
        setupUI()

    }

    private fun setupUI() {
        simpleDateFormat = SimpleDateFormat(getString(R.string.message_time_format), Locale.getDefault())
        textToSpeech = TextToSpeech(this, null)

        messageListRecyclerView.adapter = adapter
        setRecordButtonTouchListener()
    }

    private fun setupSpeechRecognizerIntent() {
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(v: Float) {}

            override fun onBufferReceived(bytes: ByteArray) {}

            override fun onEndOfSpeech() {}

            override fun onError(i: Int) {}

            override fun onResults(bundle: Bundle) {
                val matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (matches != null && matches.isNotEmpty()) {
                    postMessage(matches.first())
                }
            }

            override fun onPartialResults(bundle: Bundle) {

            }

            override fun onEvent(i: Int, bundle: Bundle) {

            }
        })
    }

    private fun setRecordButtonTouchListener() {
        messageRecordButton.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                    messageEditorEditText.setText("")
                    messageEditorEditText.hint = getString(R.string.message_editor_enter_message_hint)
                }

                MotionEvent.ACTION_DOWN -> {
                    speechRecognizer.startListening(speechRecognizerIntent)
                    messageEditorEditText.setText("")
                    messageEditorEditText.hint = getString(R.string.message_editor_listening_hint)
                }
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        textToSpeech.shutdown()
    }

    fun sendButtonClicked(view: View) {
        val message = messageEditorEditText.text.toString()

        if (!TextUtils.isEmpty(message)) {
            postMessage(message)
        }
    }

    private fun postMessage(message: String, date: String = simpleDateFormat.format(Date())) {
        adapter.addMessage(BaseMessage(message, SELF, date))
        messageListRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        messageEditorEditText.setText("")

        PostMessageTask(this).execute(message, "en", "12345")
    }

    override fun updateMessages(message: BaseMessage) {
        adapter.addMessage(message)
        messageListRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName"))
                startActivity(intent)
                finish()
            }
        }
    }

    private class PostMessageTask(val view: MessageView) : AsyncTask<String, Void, BaseMessage>() {

        override fun doInBackground(vararg params: String): BaseMessage {
            val response = MessageRepository().postMessage(params[0], params[1], params[2])
            val body = response.body()!!
            val result = body.getAsJsonObject(RESULT)

            val timeStamp = body.get(TIME_STAMP).asString
            val source = result.get(SOURCE).asString
            val speech = result.get(SPEECH).asString

            return BaseMessage(speech, source, timeStamp)
        }

        override fun onPostExecute(result: BaseMessage) {
            super.onPostExecute(result)
            view.updateMessages(result)
        }

    }

    companion object {
        private const val TIME_STAMP = "timestamp"
        private const val RESULT = "result"
        private const val SPEECH = "speech"
        private const val SOURCE = "source"
    }

}
