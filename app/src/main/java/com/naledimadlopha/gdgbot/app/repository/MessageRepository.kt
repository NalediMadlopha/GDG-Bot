package com.naledimadlopha.gdgbot.app.repository

import com.google.gson.JsonObject
import com.naledimadlopha.gdgbot.app.service.Service
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MessageRepository {

    fun postMessage(message: String, lang: String, sessionId: String): Response<JsonObject> {

        val service = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create<Service>(Service::class.java)

        return service.postMessage(message, lang, sessionId).execute()

    }

    companion object {
        private const val BASE_URL = "https://api.dialogflow.com/v1/"
    }

}