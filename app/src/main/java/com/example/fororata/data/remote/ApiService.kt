package com.example.fororata.data.remote

import com.example.fororata.data.dataModel.Post
import retrofit2.http.GET

interface ApiService {
    @GET("/posts")
    suspend fun getPost(): List<Post>

    @GET("posts?_limit=10")
    suspend fun getTopPosts(): List<Post>
}

