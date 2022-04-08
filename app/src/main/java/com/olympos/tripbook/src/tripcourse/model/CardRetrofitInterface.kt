package com.olympos.tripbook.src.tripcourse.model

import retrofit2.Call
import retrofit2.http.*

interface CardRetrofitInterface {
    @POST("/app/course/{userIdx}")
    fun postCard(@Path("userIdx")userIdx : Int, @Body card : Card): Call<PostCardResponse>
    //Gson 객체로 바꿔서 서버로 보내는 어노테이션 : @Body

    @PATCH("/app/course/tripImg/{userIdx}/{tripIdx}")
    fun patchTripImg(@Path("userIdx") userIdx : Int, @Path("tripIdx") tripIdx: Int, @Body params : HashMap<String, Any>) : Call<ServerDefaultResponse>

    @PATCH("/app/trip/deleteTrip/{userIdx}/{tripIdx}")
    fun deleteTrip(@Path("userIdx") userIdx : Int, @Path("tripIdx") tripIdx: Int) : Call<ServerDefaultResponse>



    @PATCH("/app/course/deleteCourse/{userIdx}/{courseIdx}")
    fun deleteCard(@Path("userIdx") userIdx : Int, @Path("courseIdx") courseIdx :String) : Call<ServerDefaultResponse>

    @GET("/app/trip/{userIdx}/{tripIdx}/courses")
    fun getTripcourses(@Path("userIdx") userIdx: Int, @Path("tripIdx") tripIdx: String): Call<GetTripcourseResponse>

    @PATCH("/app/course/courseDate/{userIdx}/{courseIdx}")
    fun patchDate(@Path("userIdx") userIdx : String, @Path("courseIdx") courseIdx :String, @Body params : HashMap<String, Any>) : Call<ServerDefaultResponse>

    @PATCH("/app/course/courseTime/{userIdx}/{courseIdx}")
    fun patchTime(@Path("userIdx") userIdx : String, @Path("courseIdx") courseIdx : String, @Body params : HashMap<String, Any>) : Call<ServerDefaultResponse>

    @PATCH("/app/course/courseTitle/{userIdx}/{courseIdx}")
    fun patchTitle(@Path("userIdx") userIdx : String, @Path("courseIdx") courseIdx:String, @Body params : HashMap<String, Any> ) : Call<ServerDefaultResponse>

    @PATCH("/app/course/courseImg/{userIdx}/{courseIdx}")
    fun patchImg(@Path("userIdx") userIdx : String, @Path("courseIdx") courseIdx:String, @Body params : HashMap<String, Any>) : Call<ServerDefaultResponse>

    @PATCH("/app/course/courseComment/{userIdx}/{courseIdx}")
    fun patchBody(@Path("userIdx") userIdx : String, @Path("courseIdx") courseIdx:String, @Body params : HashMap<String, Any>) : Call<ServerDefaultResponse>
}