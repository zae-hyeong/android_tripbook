package com.olympos.tripbook.src.trip.controller

import com.olympos.tripbook.src.home.model.HomeResponse
import retrofit2.Call
import retrofit2.http.*

interface GetTripCountApi {
    @GET("/app/trips/count/{userIdx}")
    fun getTripCount(@Path("userIdx") userIdx: String): Call<HomeResponse>
}