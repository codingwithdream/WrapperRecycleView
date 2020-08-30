package com.dreamxu.wrapperrecycleview.contract

import com.dreamxu.wrapperrecycleview.bean.GankResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface GankURLService {
    companion object {
        const val BASE_URL = "http://gank.io/api/v2/data/category/GanHuo/"
    }

    @GET("type/{type}/page/{page}/count/{count}")
    fun requestData(
        @Path("type") type: String?,
        @Path("page") page: Int,
        @Path("count") count: Int): Observable<GankResponse>
}
