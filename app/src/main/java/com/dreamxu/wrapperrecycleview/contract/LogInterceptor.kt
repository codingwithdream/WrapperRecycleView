package com.dreamxu.wrapperrecycleview.contract

import android.util.Log
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response

class LogInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // the time to send request
        val t1: Long = System.nanoTime()

        val method = request.method()
        if (method == "POST") {
            val sb = StringBuilder()
            if (request.body() is FormBody) {
                val body = request.body() as FormBody?
                for (i in 0 until body!!.size()) {
                    sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",")
                }
                sb.delete(sb.length - 1, sb.length)
                Log.d(
                    "CSDN_LQR", String.format(
                        "Send Request: %s on %s %n%s %nRequestParams:{%s}", request.url(), chain.connection(), request.headers(), sb.toString()
                    )
                )
            }
        } else {
            Log.d(
                "CSDN_LQR", String.format(
                    "Send Request: %s on %s %n%s %n", request.url(), chain.connection(), request.headers()))
        }

        // the time to receive response
        val t2 = System.nanoTime()
        val response = chain.proceed(request)
        //can not use response.body().string() to print log
        //because after using response.body().string()，the stream of response would be closed，
        // the app will crash, so we need to build a new response for application layer to process
        val responseBody = response.peekBody(1024 * 1024)
        Log.d("CSDN_LQR", String.format(
                "Receive Response: [%s] %n return json:【%s】 %.1fms %n%s", response.request().url(), responseBody.string(), (t2 - t1) / 1e6, response.headers()))
        return response
    }
}
