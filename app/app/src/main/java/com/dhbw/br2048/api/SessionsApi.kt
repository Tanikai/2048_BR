package com.dhbw.br2048.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SessionsApi {
    @GET("OpenSessionsList")
    fun getOpenSessionsListFromApi(): Call<SessionsApiResult>
}