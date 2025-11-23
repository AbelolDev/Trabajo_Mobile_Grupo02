package com.example.fororata.data.repository

import com.example.fororata.data.dataModel.Post
import com.example.fororata.data.remote.RetrofitInstance

class PostRepository {
    suspend fun getPosts(): List<Post>{
        return RetrofitInstance.api.getPost()
    }
}