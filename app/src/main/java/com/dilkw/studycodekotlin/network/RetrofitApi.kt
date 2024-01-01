package com.dilkw.studycodekotlin.network

import com.dilkw.studycodekotlin.network.dao.RepoReleaseTag
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitApi {

    @GET("square/retrofit/releases")
    fun getInformationFromBaidu(): Call<List<Repo>>

    @GET("square/retrofit/tags")
    fun getRepoReleaseTags(): Call<List<RepoReleaseTag>>

}