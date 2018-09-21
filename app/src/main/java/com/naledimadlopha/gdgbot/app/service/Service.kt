package com.naledimadlopha.gdgbot.app.service

import com.google.gson.JsonObject
import com.naledimadlopha.gdgbot.app.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Service {

    @Headers("Authorization:Bearer " + BuildConfig.GDG_BOT_DIALOGFLOW_API_KEY, "Content-Type:application/json")
    @GET("query")
    fun postMessage(@Query("query") message: String, @Query("lang") lang: String, @Query("sessionId") sessionId: String): Call<JsonObject>

}
