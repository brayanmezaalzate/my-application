package com.example.myapplication.network

import com.example.myapplication.model.Task
import retrofit2.http.GET

interface ApiService {

    @GET("todos")
    suspend fun getTasks(): List<Task>
}