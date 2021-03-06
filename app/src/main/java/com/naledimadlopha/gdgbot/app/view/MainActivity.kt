package com.naledimadlopha.gdgbot.app.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import android.widget.Toast
import com.naledimadlopha.gdgbot.app.R
import com.naledimadlopha.gdgbot.app.model.BaseMessage
import com.naledimadlopha.gdgbot.app.util.Utils.Companion.isConnected
import com.naledimadlopha.gdgbot.app.viewmodel.MessageViewModel
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), MessageView {

    private val adapter: MessageListAdapter = MessageListAdapter(ArrayList())
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private lateinit var viewModel: MessageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermission()
        setupSpeechRecognizerIntent()
        setupUI()

        viewModel = MessageViewModel(this)
    }

    private fun setupUI() {
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
                    if (isConnected(applicationContext)) {
                        viewModel.postMessage(matches.first())
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onPartialResults(bundle: Bundle) {

            }

            override fun onEvent(i: Int, bundle: Bundle) {

            }
        })
    }

    private fun readMessage(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
    }

    private fun setRecordButtonTouchListener() {
        messageRecordButton.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    speechRecognizer.stopListening()
                    messageEditorEditText.text = null
                    messageEditorEditText.hint = getString(R.string.message_editor_enter_message_hint)
                }

                MotionEvent.ACTION_DOWN -> {
                    speechRecognizer.startListening(speechRecognizerIntent)
                    messageEditorEditText.text = null
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

    override fun updateMessages(message: BaseMessage) {
        adapter.addMessage(message)
        messageListRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)

        if (message.sender != SELF) {
            readMessage(message.message)
        }
    }

    fun sendButtonClicked(view: View) {
        val message = messageEditorEditText.text.toString()

        if (!TextUtils.isEmpty(message)) {
            if (isConnected(this)) {
                messageEditorEditText.setText("")
                viewModel.postMessage(message)
            } else {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
            }
        }
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

    companion object {
        private const val SELF = "self"
        private const val UTTERANCE_ID = "id1"
    }

}
