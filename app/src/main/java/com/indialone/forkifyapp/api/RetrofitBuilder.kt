package com.indialone.forkifyapp.api

import com.google.gson.GsonBuilder
import com.indialone.forkifyapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private fun getInstance(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()


        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService = getInstance().create(ApiService::class.java)

}